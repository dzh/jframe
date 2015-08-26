/**
 * 
 */
package jframe.jedis.service;

import jframe.core.plugin.annotation.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author dzh
 * @date Dec 2, 2014 10:37:37 AM
 * @since 1.0
 */
@Service(clazz = "jframe.jedis.service.JedisServiceImpl", id = "jframe.service.jedis")
public interface JedisService {

	/**
	 * 
	 * @return jedis.host.default's jedis
	 */
	Jedis getJedis();

	Jedis getJedis(String name);

	void recycleJedis(Jedis jedis);

	void recycleJedis(String name, Jedis jedis);

	/**
	 * unsupported
	 * 
	 * @param name
	 * @return
	 */
	JedisCluster getJedisCluster(String name);

}
