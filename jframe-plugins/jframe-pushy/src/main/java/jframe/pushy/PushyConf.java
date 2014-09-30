/**
 * 
 */
package jframe.pushy;

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

	public static final String KEY_IOS_AUTH = "ios.auth";
	public static final String KEY_IOS_PASSWORD = "ios.password";
	public static final String KEY_HOST = "host";
	public static final String KEY_HOST_PORT = "host.port";
	public static final String KEY_FEEDBACK = "feedback";
	public static final String KEY_FEEDBACK_PORT = "feedback.post";

	synchronized static void init(InputStream is) throws Exception {
		PropertyResourceBundle props = new PropertyResourceBundle(is);
		try {
			IOS_AUTH = props.getString(KEY_IOS_AUTH);
			IOS_PASSWORD = props.getString(KEY_IOS_PASSWORD);
			HOST = props.getString(KEY_HOST);
			HOST_PORT = props.getString(KEY_HOST_PORT);
			FEEDBACK = props.getString(KEY_FEEDBACK);
			FEEDBACK_PORT = props.getString(KEY_FEEDBACK_PORT);
		} catch (MissingResourceException e) {
			throw e;
		}
	}

}
