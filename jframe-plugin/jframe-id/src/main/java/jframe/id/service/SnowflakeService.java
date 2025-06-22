package jframe.id.service;

import jframe.core.conf.Config;
import jframe.core.plugin.annotation.*;
import jframe.core.util.PropsConf;
import jframe.id.IdField;
import jframe.id.IdPlugin;
import jframe.zk.service.CuratorService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dzh
 * @date 2025/6/21 16:12
 */
@Injector
public class SnowflakeService implements IdService {

    static Logger LOG = LoggerFactory.getLogger(SnowflakeService.class);

    @InjectPlugin
    static IdPlugin Plugin;

    @InjectService(id = CuratorService.ID)
    static CuratorService ZK;

    static String FILE_SNOWFLAKE = "file.snowflake";

    static PropsConf _config = new PropsConf();

    static SnowflakeIdGenerator _generator;

    private long workerId;

    @Start
    void start() {
        LOG.info("SnowflakeService startup...");

        try {
            String file = Plugin.getConfig(FILE_SNOWFLAKE, Plugin.getConfig(Config.APP_CONF) + "/snowflake.properties");
            if (!new File(file).exists()) {
                throw new FileNotFoundException("not found " + file);
            }
            _config.init(file);
            LOG.info("load snowflake config {}", _config);

            if (disabled()) {
                LOG.info("SnowflakeService is disabled");
                return;
            }
            this.workerId = workerId();
            _generator = new SnowflakeIdGenerator(epoch(), dataCenterId(), this.workerId, timestampBits(), dataCenterBits(), workerBits(), sequenceBits());
        } catch (Exception e) {
            LOG.error("SnowflakeService startup failed!", e);
            return;
        }
        LOG.info("SnowflakeService startup success!");
    }

    protected boolean disabled() {
        return _config.getConfBool(null, IdField.SNOWFLAKE_DISABLED, "false");
    }

    protected String epoch() {
        return _config.getConf(null, IdField.SNOWFLAKE_EPOCH, "2025-06-01T00:00:00Z");
    }

    protected long dataCenterId() {
        return _config.getConfLong(null, IdField.SNOWFLAKE_DATACENTER_ID, "0");
    }

    protected long workerId() {
        String workerGeneration = _config.getConf(null, IdField.SNOWFLAKE_WORKER_GENERATION, "");
        switch (workerGeneration) {
            case IdField.GENERATION_IP:
                return generateWorkerIdFromIP();
            case IdField.GENERATION_ZK:
                return generateWorkerIdFromZK();
            default:
                return _config.getConfLong(null, IdField.SNOWFLAKE_WORKER_ID, "0");
        }
    }

    private boolean isZK() {
        String workerGeneration = _config.getConf(null, IdField.SNOWFLAKE_WORKER_GENERATION, "");
        return IdField.GENERATION_ZK.equals(workerGeneration);
    }

    protected String zkId() {
        return _config.getConf(null, IdField.SNOWFLAKE_ZK_ID, IdField.ZK_ID);
    }

    protected String workerZKPath() {
        return _config.getConf(null, IdField.SNOWFLAKE_ZK_WORKER_PATH, IdField.ZK_WORKER_PATH);
    }

    protected long generateWorkerIdFromZK() {
        try {
            CuratorFramework zk = ZK.client(zkId());
            String workerIdPath = workerZKPath(); //parent path
            if (zk.checkExists().forPath(workerIdPath) == null) {
                zk.create().creatingParentsIfNeeded().forPath(workerIdPath);
            }
            // 获取当前所有已分配的ID
            List<String> existingNodes = zk.getChildren().forPath(workerIdPath);
            LOG.info("existing workers {}", existingNodes);
            Set<Long> usedIds = new HashSet<>();
            // 解析现有节点中的ID
            for (String node : existingNodes) {
                try {
                    long id = Long.parseLong(node.substring("worker-".length()));
                    usedIds.add(id);
                } catch (NumberFormatException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            long maxWorkerId = maxWorkerId();
            // 查找最小的可用ID
            for (long id = 0; id <= maxWorkerId; id++) {
                if (!usedIds.contains(id)) {
                    // 尝试创建该ID对应的节点
                    String path = workerIdZKPath(id);
                    try {
                        zk.create().withMode(CreateMode.EPHEMERAL).forPath(path);
                        LOG.info("generate workerId {} from zk {}", id, path);
                        return id;
                    } catch (KeeperException.NodeExistsException e) {
                        // 另一个进程可能已经占用了这个ID，继续尝试下一个
                        LOG.warn(e.getMessage(), e);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return -1L;
    }

    protected long generateWorkerIdFromIP() {
        String ip = ip();
        if (ip == null || ip.isEmpty()) {
            LOG.error("No valid non-loopback IP address found");
            return -1L;
        }

        // 解析IP地址的最后两段
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ip);
        }

        // 提取最后两段作为基础
        int segment3 = Integer.parseInt(parts[2]);
        int segment4 = Integer.parseInt(parts[3]);

        // 使用简单的哈希算法生成唯一ID，这里使用异或和移位确保分布更均匀
//        long dataCenterId = (segment3 ^ ((long) segment4 << 4)) % (maxDataCenterId() + 1);
        long workerId = (segment4 ^ ((long) segment3 << 4)) % (maxWorkerId() + 1);
        // 确保生成的ID为正数
//        dataCenterId = Math.abs(dataCenterId);
        workerId = Math.abs(workerId);

//        LOG.info("generateDataCenterAndworkerId {} {} {}", ip, dataCenterId, workerId);
//        return new long[]{dataCenterId, workerId};
        return workerId;
    }


    protected int timestampBits() {
        return _config.getConfInt(null, IdField.SNOWFLAKE_TIMESTAMP_BITS, "41");
    }

    protected int dataCenterBits() {
        return _config.getConfInt(null, IdField.SNOWFLAKE_DATACENTER_BITS, "5");
    }

    protected int workerBits() {
        return _config.getConfInt(null, IdField.SNOWFLAKE_WORKER_BITS, "5");
    }

    protected int sequenceBits() {
        return _config.getConfInt(null, IdField.SNOWFLAKE_SEQUENCE_BITS, "12");
    }

    @Override
    public long nextId() {
        if (_generator == null || disabled()) return 0L;
        return _generator.nextId();
    }

    protected String ip() {
        String ip = System.getenv(ipEnv());
        if (ip == null || ip.isEmpty()) {
            try {
                ip = nonLoopbackIP();
            } catch (SocketException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return ip;
    }

    protected String ipEnv() {
        return _config.getConf(null, IdField.SNOWFLAKE_WORKER_ENV_IP, IdField.ENV_HOST_IP);
    }

    // 获取非回环IP地址（保持不变）
    public static String nonLoopbackIP() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.isUp() && !ni.isLoopback() && !ni.isVirtual()) {
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    //todo 这个判断是否正确
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') == -1) {
                        return addr.getHostAddress();
                    }
                }
            }
        }

        return null;
    }

    public long maxWorkerId() {
        int workerIdBits = workerBits();
        return (1L << workerIdBits) - 1;
    }

    public long maxDataCenterId() {
        int dataCenterIdBits = dataCenterBits();
        return (1L << dataCenterIdBits) - 1;
    }

    @Stop
    void stop() {
        if (isZK()) {
            releaseWorkerId(workerId);
        }
    }

    public String workerIdZKPath(long workerId) {
        String workerIdPath = workerZKPath();
        return workerIdPath + "/worker-" + String.format("%010d", workerId);
    }

    private void releaseWorkerId(long workerId) {
        String path = workerIdZKPath(workerId);
        try {
            CuratorFramework zk = ZK.client(zkId());
            if (zk.checkExists().forPath(path) != null) {
                zk.delete().forPath(path);
                LOG.info("Worker ID {} released successfully", workerId);
            } else {
                LOG.warn("Worker ID {} already deleted", workerId);
            }
        } catch (Exception e) {
            LOG.error("Failed to release worker ID {}", workerId, e);
        }
    }

}
