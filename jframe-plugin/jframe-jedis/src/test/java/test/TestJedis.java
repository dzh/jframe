package test;

/**
 * 
 */

import jframe.jedis.service.JedisServiceImpl;
import redis.clients.jedis.Jedis;

/**
 * @author dzh
 * @date Aug 6, 2014 1:26:15 PM
 * @since 1.0
 */
public class TestJedis {

	public void testLoadConf() throws Exception {
		JedisServiceImpl impl = new JedisServiceImpl();
		impl.init(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("test/redis.properties"), true);
		impl.start();

		Jedis j = impl.getJedis("t1");
		long l = j.incr("test.pay");
		System.out.println(l);
		j.del("test.pay");

		j = impl.getJedis();
		l = j.incr("test.pay.d");
		System.out.println(l);
		j.del("test.pay.d");

		impl.stop();
	}

	public void testExpire() throws Exception {
		JedisServiceImpl impl = new JedisServiceImpl();
		impl.init(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("test/redis.properties"), true);
		impl.start();

		String key = "test.pay.expire";
		Jedis j = impl.getJedis();
		long l = j.incr(key);
		System.out.println(l);
		System.out.println(j.get(key));
		j.expire(key, 2);
		Thread.sleep(4 * 1000);
		System.out.println(j.get(key));
		impl.stop();
	}

}
