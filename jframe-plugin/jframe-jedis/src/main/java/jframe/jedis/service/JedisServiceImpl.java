/**
 * 
 */
package jframe.jedis.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.core.util.PropsConf;
import jframe.jedis.JedisPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @author dzh
 * @date Aug 6, 2014 1:31:26 PM
 * @since 1.0
 */
@Injector
public class JedisServiceImpl implements JedisService {

    static final Logger LOG = LoggerFactory.getLogger(JedisServiceImpl.class);

    private PropsConf conf = new PropsConf();

    // groupId
    private Map<String, JedisPool> _jedis = new HashMap<String, JedisPool>();

    public PropsConf init(File jedis) throws Exception {
        if (!jedis.exists()) {
            LOG.error("Not found jedis file {}", jedis.getAbsolutePath());
            throw new FileNotFoundException("jedis file not found!" + jedis.getAbsolutePath());
        }
        return init(new FileInputStream(jedis), true);
    }

    public PropsConf init(InputStream jedis, boolean closeIn) throws Exception {
        conf.init(jedis);
        return conf;
    }

    JedisPoolConfig createPoolConfig(PropsConf conf, String id) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(conf.getConfInt(id, "redis.conn.maxTotal", "200"));
        config.setMaxIdle(conf.getConfInt(id, "redis.conn.maxIdle", "100"));
        config.setMinIdle(conf.getConfInt(id, "redis.conn.minIdle", "1"));
        config.setMaxWaitMillis(conf.getConfLong(id, "redis.conn.maxWaitMillis", "3000"));
        // config.setTestOnReturn(true);
        return config;
    }

    @InjectPlugin
    static JedisPlugin plugin;

    @Start
    public void start() {
        String jedis = plugin.getConfig("file.redis", "");
        if ("".equals(jedis)) {
            LOG.error("jedis.conf not found! {}", jedis);
            return;
        }
        try {
            start(init(new File(jedis)));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void start(PropsConf conf) {
        LOG.info("JedisServiceImpl starting");
        String[] hosts = conf.getGroupIds();
        for (String h : hosts) {
            if ("".equals(h)) continue;
            try {
                String ip = conf.getConf(h, "ip", "127.0.0.1");
                // if ("127.0.0.1".equals(ip)) {
                // continue;
                // }
                int port = conf.getConfInt(h, "port", "6379");
                int timeout = conf.getConfInt(h, "timeout", "2000");
                String passwd = conf.getConf(h, "passwd").trim();
                int database = conf.getConfInt(h, "database", String.valueOf(Protocol.DEFAULT_DATABASE));// 0

                JedisPoolConfig config = createPoolConfig(conf, h);
                _jedis.put(h, new JedisPool(config, ip, port, timeout, "".equals(passwd) ? null : passwd, database));
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                continue;
            }
        }
        LOG.info("JedisServiceImpl start successfully");
    }

    @Stop
    public void stop() {
        LOG.info("JedisServiceImpl stopping");

        Iterator<String> iter = _jedis.keySet().iterator();
        while (iter.hasNext()) {
            try {
                JedisPool j = _jedis.get(iter.next());
                if (j != null) j.destroy();
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        LOG.info("JedisServiceImpl stop successfully");

        close();
    }

    /*
     * (non-Javadoc)
     * @see dono.pay.service.JedisService#getJedis(java.lang.String)
     */
    @Override
    public Jedis getJedis(String name) {
        try {
            JedisPool pool = _jedis.get(name);
            if (pool == null) return null;
            return pool.getResource();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see dono.pay.service.JedisService#getJedisCluster(java.lang.String)
     */
    @Override
    public JedisCluster getJedisCluster(String name) {
        // Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        // //Jedis Cluster will attempt to discover cluster nodes automatically
        // jedisClusterNodes.add(new HostAndPort("127.0.0.1", 7379));
        // JedisCluster jc = new JedisCluster(jedisClusterNodes);
        // jc.set("foo", "bar");
        // String value = jc.get("foo");
        return null;
    }

    void close() {
        if (conf != null) conf.clear();
    }

    /*
     * (non-Javadoc)
     * @see dono.pay.service.JedisService#getJedis()
     */
    @Override
    @Deprecated
    public Jedis getJedis() {
        try {
            JedisPool pool = _jedis.get(conf.getConf(null, "redis.host"));
            if (pool == null) return null;
            return pool.getResource();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * dono.pay.service.JedisService#recycleJedis(redis.clients.jedis.Jedis)
     */
    @Override
    @Deprecated
    public void recycleJedis(Jedis jedis) {
        recycleJedis(null, jedis);
    }

    /*
     * (non-Javadoc)
     * @see dono.pay.service.JedisService#recycleJedis(java.lang.String,
     * redis.clients.jedis.Jedis)
     */
    @Override
    @Deprecated
    public void recycleJedis(String name, Jedis jedis) {
        if (conf == null || _jedis == null || name == null) return;
        JedisPool pool = _jedis.get(name);
        if (pool == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Not found jedis name {}", name);
            }
            return;
        }
        pool.returnResource(jedis);
    }

}
