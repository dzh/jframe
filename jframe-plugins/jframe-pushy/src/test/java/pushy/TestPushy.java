/**
 * 
 */
package pushy;

import java.util.Calendar;
import java.util.Date;

import jframe.pushy.PushyConf;
import jframe.pushy.impl.PushyServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.relayrides.pushy.apns.ApnsEnvironment;
import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.PushManagerConfiguration;
import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.SSLContextUtil;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;

/**
 * @author dzh
 * @date Oct 14, 2014 11:28:58 AM
 * @since 1.0
 */
public class TestPushy {

	PushyServiceImpl pushy;

	static final Date ExpireTime = new Date(72 * 3600 * 1000);

	static String Img_Logo = "push.png";

	String token = "857309a68c3fe80751b65a0c6c9f394960ebc1a1942dc5219ec46d9e40ed5ace";

	@Before
	public void init() throws Exception {
		PushyConf.init(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("pushy/pushy.properties"));
		System.out.println(PushyConf.IOS_PASSWORD);

		PushManagerConfiguration conf = new PushManagerConfiguration();
		conf.setConcurrentConnectionCount(2);
		PushManager<SimpleApnsPushNotification> pushManager = new PushManager<SimpleApnsPushNotification>(
				ApnsEnvironment.getSandboxEnvironment(),
				SSLContextUtil.createDefaultSSLContext(PushyConf.IOS_AUTH,
						PushyConf.IOS_PASSWORD), null, null, null, conf,
				"PushManager");
		pushManager.start();
		pushy = new PushyServiceImpl();
		pushy.setPushManager(pushManager);
	}

	public static void main(String[] args) throws Exception {
		TestPushy pushy = new TestPushy();
		pushy.init();
		pushy.test();
	}

	@After
	public void stop() {
		pushy.stop();
	}

	@Test
	public void test() {
		try {
			pushApple(token, "111111111", null);

//			Thread.sleep(10 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pushApple(String token, String msg, Integer badge)
			throws Exception {
		ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
		// payloadBuilder.setLaunchImage(Img_Logo);
		payloadBuilder.setAlertBody(msg);
		payloadBuilder.setSoundFileName("default");
		payloadBuilder.setCategoryName("dzh");
		// payloadBuilder.setBadgeNumber(badge);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, 1);
		pushy.sendMessage(token,
				payloadBuilder.buildWithDefaultMaximumLength(), c.getTime());
	}

}
