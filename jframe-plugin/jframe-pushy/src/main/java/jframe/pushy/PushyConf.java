/**
 * 
 */
package jframe.pushy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 29, 2014 1:24:47 PM
 * @since 1.0
 */
public class PushyConf implements Fields {

	static final Logger LOG = LoggerFactory.getLogger(PushyConf.class);

	static boolean init = false;

	public static String IOS_AUTH;
	public static String IOS_PASSWORD;
	public static String HOST;
	public static String HOST_PORT;
	public static String FEEDBACK;
	public static String FEEDBACK_PORT;
	public static int PUSH_CONN_COUNT = 10;

	public synchronized static void init(String file) throws Exception {
		if (init)
			return;

		try {
			init(new FileInputStream(file));
		} catch (Exception e) {
			throw e;
		}
		init = true;
	}

	public synchronized static void init(InputStream is) throws Exception {
		try {
			Properties props = new Properties();
			props.load(is);
			IOS_AUTH = props.getProperty(KEY_IOS_AUTH).trim();
			IOS_PASSWORD = props.getProperty(KEY_IOS_PASSWORD).trim();
			HOST = props.getProperty(KEY_HOST).trim();
			HOST_PORT = props.getProperty(KEY_HOST_PORT).trim();
			FEEDBACK = props.getProperty(KEY_FEEDBACK).trim();
			FEEDBACK_PORT = props.getProperty(KEY_FEEDBACK_PORT).trim();
			PUSH_CONN_COUNT = Integer.parseInt(props.getProperty(
					KEY_PUSH_CONN_COUNT, "10"));
		} catch (MissingResourceException e) {
			throw e;
		} finally {
			if (is != null)
				is.close();
		}
	}

}
