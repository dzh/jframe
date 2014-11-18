/**
 * 
 */
package dono.share.memcached;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Aug 20, 2014 1:04:12 PM
 * @since 1.0
 */
public class MemcachedFactory {

	static Logger LOG = LoggerFactory.getLogger(MemcachedFactory.class);

	private MemcachedFactory() {
	}

	public static Properties load(File conf) {
		if (!conf.exists()) {
			LOG.error("Not found memcached.properties, path -> {}",
					conf.getAbsolutePath());
			return null;
		}

		InputStream is = null;
		try {
			is = new FileInputStream(conf);
			Properties _conf = new Properties();
			_conf.load(is);
			// validateConf(_conf);
			return _conf;
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
		return null;
	}

	// public static MemcachedService createService(File conf) {
	// return MemcachedServiceImpl.createInstance(load(conf));
	// }

}
