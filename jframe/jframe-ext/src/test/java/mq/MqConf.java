/**
 * 
 */
package mq;

import java.io.FileInputStream;
import java.io.InputStream;
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

	public static long SendSleepTime = 6;

	public static long RecvSleepTime = 6;

	static boolean init = false;

	static MsgTransfer Transfer;

	public static int DeliveryMode = 2;

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
					"UseAsyncSend", "false").trim());
			CreateConnectionOnStartup = Boolean.parseBoolean(props.getProperty(
					"CreateConnectionOnStartup", "false").trim());
			MaxConnections = Integer.parseInt(props.getProperty(
					"MaxConnections", "10").trim());
			MaximumActiveSessionPerConnection = Integer.parseInt(props
					.getProperty("MaximumActiveSessionPerConnection", "100")
					.trim());

			// consumer
			ConsumerTimeout = Long.parseLong(props.getProperty(
					"ConsumerTimeout", "60000").trim());

			SendSleepTime = Long.parseLong(props.getProperty("SendSleepTime",
					"6").trim());
			RecvSleepTime = Long.parseLong(props.getProperty("RecvSleepTime",
					"6").trim());

			// transfer
			Transfer = (MsgTransfer) Class.forName(
					props.getProperty("MsgTransfer",
							TextMsgTransfer.class.getName())).newInstance();

			// message
			DeliveryMode = Integer.parseInt(props.getProperty("DeliveryMode",
					String.valueOf(javax.jms.DeliveryMode.PERSISTENT)));
		} catch (Exception e) {
			throw e;
		} finally {
			is.close();
		}
	}

}
