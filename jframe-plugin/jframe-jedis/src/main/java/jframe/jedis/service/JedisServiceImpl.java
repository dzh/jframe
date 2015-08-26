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
import java.util.Properties;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.jedis.JedisPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private Properties conf = new Properties();

	private JedisPoolConfig _config;

	private Map<String, JedisPool> _jedis = new HashMap<String, JedisPool>();

	public void init(File jedis) throws Exception {
		if (!jedis.exists()) {
			LOG.error("Not found jedis file {}", jedis.getAbsolutePath());
			throw new FileNotFoundException("jedis file not found!"
					+ jedis.getAbsolutePath());
		}
		init(new FileInputStream(jedis), true);
	}

	public void init(InputStream jedis, boolean closeIn) throws Exception {
		try {
			conf.load(jedis);
		} finally {
			if (jedis != null && closeIn)
				jedis.close();
		}
		_config = createPoolConfig();
	}

	JedisPoolConfig createPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(100);
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
			init(new File(jedis));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		LOG.info("JedisServiceImpl starting");
		String[] hosts = conf.getProperty("redis.hosts").split("\\s");
		for (String h : hosts) {
			if ("".equals(h))
				continue;
			try {
				String ip = conf.getProperty("redis.host." + h + ".ip",
						"127.0.0.1").trim();
				// if ("127.0.0.1".equals(ip)) {
				// continue;
				// }
				Integer port = Integer.parseInt(conf.getProperty(
						"redis.host." + h + ".port", "0").trim());

				if (port == 0) {
					_jedis.put(h, new JedisPool(_config, ip,
							Protocol.DEFAULT_PORT, 6000));
				} else {
					_jedis.put(h, new JedisPool(_config, ip, port, 6000));
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
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
				if (j != null)
					j.destroy();
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
		}
		LOG.info("JedisServiceImpl stop successfully");

		close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dono.pay.service.JedisService#getJedis(java.lang.String)
	 */
	@Override
	public Jedis getJedis(String name) {
		try {
			JedisPool pool = _jedis.get(name);
			if (pool == null)
				return null;
			return pool.getResource();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		if (conf != null)
			conf.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dono.pay.service.JedisService#getJedis()
	 */
	@Override
	public Jedis getJedis() {
		try {
			JedisPool pool = _jedis.get(conf.getProperty("redis.host.default",
					""));
			if (pool == null)
				return null;
			return pool.getResource();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dono.pay.service.JedisService#recycleJedis(redis.clients.jedis.Jedis)
	 */
	@Override
	public void recycleJedis(Jedis jedis) {
		recycleJedis(null, jedis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dono.pay.service.JedisService#recycleJedis(java.lang.String,
	 * redis.clients.jedis.Jedis)
	 */
	@Override
	public void recycleJedis(String name, Jedis jedis) {
		if (conf == null || _jedis == null)
			return;
		name = name == null ? conf.getProperty("redis.host.default", "") : name;
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
