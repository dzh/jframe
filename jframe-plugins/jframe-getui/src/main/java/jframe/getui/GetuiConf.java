/**
 * 
 */
package jframe.getui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 29, 2014 1:24:47 PM
 * @since 1.0
 */
public class GetuiConf {

	static final Logger LOG = LoggerFactory.getLogger(GetuiConf.class);

	public static String APPID;
	public static String APPKEY;
	public static String MASTER_SECRET;
	public static String HOST;

	static boolean init = false;

	public synchronized static void init(String file) throws Exception {
		if (init)
			return;

		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			init(fis);
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
		init = true;
	}

	public static final String KEY_APPID = "app.id";
	public static final String KEY_APPKEY = "app.key";
	public static final String KEY_MASTER_SCRET = "master_secret";
	public static final String KEY_HOST = "host";

	synchronized static void init(InputStream is) throws IOException {
		PropertyResourceBundle props = new PropertyResourceBundle(is);
		try {
			APPID = props.getString(KEY_APPID);
			APPKEY = props.getString(KEY_APPKEY);
			MASTER_SECRET = props.getString(KEY_MASTER_SCRET);
			HOST = props.getString(KEY_HOST);
		} catch (MissingResourceException e) {
			LOG.error(e.getMessage());
		}
	}

}
