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
public class PushyConf {

	static final Logger LOG = LoggerFactory.getLogger(PushyConf.class);

	static boolean init = false;

	public static String IOS_AUTH;
	public static String IOS_PASSWORD;
	public static String HOST;
	public static String HOST_PORT;
	public static String FEEDBACK;
	public static String FEEDBACK_PORT;

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

	public static final String KEY_IOS_AUTH = "ios.auth";
	public static final String KEY_IOS_PASSWORD = "ios.password";
	public static final String KEY_HOST = "host";
	public static final String KEY_HOST_PORT = "host.port";
	public static final String KEY_FEEDBACK = "feedback";
	public static final String KEY_FEEDBACK_PORT = "feedback.port";

	public synchronized static void init(InputStream is) throws Exception {
		try {
			Properties props = new Properties();
			IOS_AUTH = props.getProperty(KEY_IOS_AUTH).trim();
			IOS_PASSWORD = props.getProperty(KEY_IOS_PASSWORD).trim();
			HOST = props.getProperty(KEY_HOST).trim();
			HOST_PORT = props.getProperty(KEY_HOST_PORT).trim();
			FEEDBACK = props.getProperty(KEY_FEEDBACK).trim();
			FEEDBACK_PORT = props.getProperty(KEY_FEEDBACK_PORT).trim();
		} catch (MissingResourceException e) {
			throw e;
		} finally {
			if (is != null)
				is.close();
		}
	}

}
