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

    public JedisPoolConfig init(File jedis) throws Exception {
        if (!jedis.exists()) {
            LOG.error("Not found jedis file {}", jedis.getAbsolutePath());
            throw new FileNotFoundException("jedis file not found!" + jedis.getAbsolutePath());
        }
        return init(new FileInputStream(jedis), true);
    }

    public JedisPoolConfig init(InputStream jedis, boolean closeIn) throws Exception {
        conf.init(jedis);
        return createPoolConfig();
    }

    JedisPoolConfig createPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(200);
        config.setMaxIdle(10);
        config.setMinIdle(1);
        config.setMaxWaitMillis(3000L);
        config.setTestOnBorrow(true);
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

    public void start(JedisPoolConfig config) {
        LOG.info("JedisServiceImpl starting");
        String[] hosts = conf.getGroupIds();
        for (String h : hosts) {
            if ("".equals(h)) continue;
            try {
                String ip = conf.getConf(h, "ip");
                // if ("127.0.0.1".equals(ip)) {
                // continue;
                // }
                int port = conf.getConfInt(h, "port", "6379");
                int timeout = conf.getConfInt(h, "timeout", "2000");
                String passwd = conf.getConf(h, "passwd").trim();

                _jedis.put(h, new JedisPool(config, ip, port, timeout, "".equals(passwd) ? null : passwd, Protocol.DEFAULT_DATABASE));
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
    public void recycleJedis(Jedis jedis) {
        recycleJedis(null, jedis);
    }

    /*
     * (non-Javadoc)
     * @see dono.pay.service.JedisService#recycleJedis(java.lang.String,
     * redis.clients.jedis.Jedis)
     */
    @Override
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
