/**
 * 
 */
package jframe.ext.dispatch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Oct 16, 2014 12:21:47 PM
 * @since 1.0
 */
public class MqConf {

	static final Logger LOG = LoggerFactory.getLogger(MqConf.class);

	/**
	 * 默认发送队列
	 */
	public static String DefMqSendQueue;

	/**
	 * 默认接收队列
	 */
	public static String DefMqRecvQueue;

	/**
	 * mq地址
	 */
	public static String MqUrl;

	/**************************** MQ配置 *****************************/
	public static boolean UseAsyncSend;
	public static boolean CreateConnectionOnStartup;
	public static int MaxConnections;
	public static int MaximumActiveSessionPerConnection;

	public static long ConsumerTimeout = 60000;

	/**************************************************************/

	static boolean init = false;

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

	public synchronized static void init(InputStream is) throws IOException {
		if (is == null)
			return;
		Properties props = new Properties();
		try {
			props.load(is);
			DefMqSendQueue = props.getProperty("DefMqSendQueue");
			DefMqRecvQueue = props.getProperty("DefMqRecvQueue");

			// producer
			MqUrl = props.getProperty("MqUrl");
			UseAsyncSend = Boolean.parseBoolean(props.getProperty(
					"UseAsyncSend", "false"));
			CreateConnectionOnStartup = Boolean.parseBoolean(props.getProperty(
					"CreateConnectionOnStartup", "false"));
			MaxConnections = Integer.parseInt(props.getProperty(
					"MaxConnections", "10"));
			MaximumActiveSessionPerConnection = Integer.parseInt(props
					.getProperty("MaximumActiveSessionPerConnection", "100"));

			// consumer
			ConsumerTimeout = Long.parseLong(props.getProperty(
					"ConsumerTimeout", "60000"));
		} catch (MissingResourceException e) {
			LOG.error(e.getMessage());
		} finally {
			is.close();
		}
	}

}
