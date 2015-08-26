/**
 * 
 */
package jframe.memcached.client;

import java.util.Date;

import jframe.core.plugin.annotation.Service;

/**
 * 
 * 
 * @author dzh
 * @date Aug 16, 2014 4:41:46 PM
 * @since 1.0
 */
@Service(clazz = "jframe.memcached.client.MemcachedServiceImpl", id = "jframe.service.memcached.client")
public interface MemcachedService {

	boolean add(String key, Object value, Date expiry);

	boolean add(String key, Object value);

	boolean set(String key, Object value, Date expiry);

	boolean set(String key, Object value);

	Object get(String key);

	boolean delete(String key);

	long decr(String key, long inc);

	long decr(String key);

	long incr(String key);

	long incr(String key, long inc);

	boolean append(String key, Object value);

	boolean prepend(String key, Object value);

}
