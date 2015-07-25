/**
 * 
 */
package jframe.memcached.client;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

/**
 * TODO 对于没有找到的處理
 * 
 * @author dzh
 * @date Sep 17, 2014 2:18:48 PM
 * @since 1.0
 */
@Injector
public class MemcachedServiceImpl implements MemcachedService {

	static final String FILE_CONF = "file.memcached";

	static final Logger LOG = LoggerFactory
			.getLogger(MemcachedServiceImpl.class);

	private SockIOPool _pool;

	public SockIOPool getPool() {
		return _pool;
	}

	public void setPool(SockIOPool pool) {
		this._pool = pool;
	}

	private Properties _conf;

	public Properties getConf() {
		return _conf;
	}

	public void setConf(Properties _conf) {
		this._conf = _conf;
	}

	private MemCachedClient mcc;

	@InjectPlugin
	static MemcachedPlugin plugin;

	/**
	 * 
	 */
	@Start
	public void start() {
		String path = plugin.getConfig(FILE_CONF, "");
		Properties _conf = MemcachedFactory.load(new File(path));
		if (_conf == null) {
			LOG.error("Not found memcached.file {}", path);
			return;
		}

		String pname = _conf.getProperty("mem.name");

		SockIOPool _pool = createPool(_conf);
		_pool.initialize();
		LOG.info("SockIOPool {} initialize successfully!", pname);

		mcc = new MemCachedClient(pname);
	}

	private static SockIOPool createPool(Properties _conf) {
		String pname = _conf.getProperty("mem.name");

		SockIOPool _pool = SockIOPool.getInstance(pname);
		String[] servers = _conf.getProperty("mem.servers").split(",");
		String[] memServers = new String[servers.length];
		Integer[] memWeights = new Integer[servers.length];
		for (int i = 0; i < servers.length; i++) {
			memServers[i] = _conf.getProperty("mem.server." + servers[i].trim()
					+ ".host", "127.0.0.1")
					+ ":"
					+ _conf.getProperty("mem.server." + servers[i].trim()
							+ ".port", "11211");
			memWeights[i] = Integer.parseInt(_conf.getProperty("mem."
					+ servers[i] + ".weight", "1"));

			LOG.info("memached -> {}", memServers[i]);
		}
		_pool.setServers(memServers);
		_pool.setWeights(memWeights);

		_pool.setInitConn(Integer.parseInt(_conf.getProperty("mem.initconn",
				"10")));
		_pool.setMinConn(Integer.parseInt(_conf.getProperty("mem.minconn", "5")));
		_pool.setMaxConn(Integer.parseInt(_conf.getProperty("mem.maxconn",
				"250")));
		_pool.setMaxIdle(Integer.parseInt(_conf.getProperty("mem.maxidle",
				"3600000")));

		_pool.setMaintSleep(30);
		_pool.setNagle(false);
		_pool.setSocketTO(Integer.parseInt(_conf.getProperty(
				"mem.timeout.read", "3000")));
		_pool.setSocketConnectTO(Integer.parseInt(_conf.getProperty(
				"mem.timeout.conn", "3000")));
		return _pool;
	}

	@Stop
	public void stop() {
		clearConf();
		if (_pool != null)
			_pool.shutDown();
	}

	private void clearConf() {
		if (_conf != null)
			_conf.clear();
	}

	@Override
	public boolean add(String key, Object value, Date expiry) {
		return mcc.add(key, value, expiry);
	}

	@Override
	public boolean add(String key, Object value) {
		return mcc.add(key, value);
	}

	@Override
	public boolean set(String key, Object value, Date expiry) {
		return mcc.set(key, value, expiry);
	}

	@Override
	public boolean set(String key, Object value) {
		return mcc.set(key, value);
	}

	@Override
	public Object get(String key) {
		return mcc.get(key);
	}

	@Override
	public boolean delete(String key) {
		return mcc.delete(key);
	}

	@Override
	public long decr(String key, long inc) {
		return mcc.decr(key, inc);
	}

	@Override
	public long decr(String key) {
		return mcc.decr(key);
	}

	@Override
	public long incr(String key) {
		return mcc.incr(key);
	}

	@Override
	public long incr(String key, long inc) {
		return mcc.incr(key, inc);
	}

	@Override
	public boolean append(String key, Object value) {
		return mcc.append(key, value);
	}

	@Override
	public boolean prepend(String key, Object value) {
		return mcc.prepend(key, value);
	}

}
