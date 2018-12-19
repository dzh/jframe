/**
 * 
 */
package jframe.zk.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.core.util.PropsConf;
import jframe.zk.ZkField;
import jframe.zk.ZkPlugin;
import jframe.zk.service.CuratorService;

/**
 * @author dzh
 * @date May 4, 2016 5:35:58 PM
 * @since 1.0
 */
@Injector
public class CuratorServiceImpl implements CuratorService {

    static Logger LOG = LoggerFactory.getLogger(CuratorServiceImpl.class);

    @InjectPlugin
    static ZkPlugin Plugin;

    private Map<String, CuratorFramework> clients = new HashMap<>(8);

    static String FILE_CURATOR = "file.curator";

    static PropsConf _config = new PropsConf();

    @Start
    void start() {
        LOG.info("Start CuratorService");

        try {
            String file = Plugin.getConfig(FILE_CURATOR, Plugin.getConfig(Config.APP_CONF) + "/curator.properties");
            if (!new File(file).exists()) { throw new FileNotFoundException("not found " + file); }
            _config.init(file);
            for (String id : _config.getGroupIds()) {
                String connectString = _config.getConf(id, ZkField.ConnectString);
                String ns = _config.getConf(id, ZkField.Namespace, "ns");
                int connectTimeout = _config.getConfInt(id, ZkField.ConnectTimeout, "5000");
                int sessionTimeout = _config.getConfInt(id, ZkField.SessionTimeout, "30000");

                int retryInterval = _config.getConfInt(id, ZkField.RetryInterval, "5000");
                int retryTimes = _config.getConfInt(id, ZkField.RetryTimes, "3");
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(retryInterval, retryTimes);
                CuratorFramework zkCli = CuratorFrameworkFactory.builder().connectString(connectString).retryPolicy(retryPolicy)
                        .connectionTimeoutMs(connectTimeout).sessionTimeoutMs(sessionTimeout).namespace(ns).build();
                try {
                    zkCli.start();
                    zkCli.blockUntilConnected(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                    zkCli.close();
                }
                if (zkCli.getState() == CuratorFrameworkState.STARTED) clients.put(id, zkCli);
            }
        } catch (Exception e) {
            LOG.error("Start CuratorService Failure!" + e.getMessage(), e);
            return;
        }
        LOG.info("Start CuratorService Successfully!");
    }

    @Stop
    void stop() {
        clients.forEach((k, v) -> {
            v.close();
        });
    }

    @Override
    public CuratorFramework client(String id) {
        return clients.get(id);
    }

}
