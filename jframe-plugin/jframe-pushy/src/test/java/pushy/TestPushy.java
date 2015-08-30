/**
 * 
 */
package pushy;

import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

	String token = "70854405ac6b60b64bdc5338a2d8f4a55a683f63e786a872be42454f6731618d";

	ExecutorService exeSvc;

	NioEventLoopGroup eventGroup;

	@Before
	public void init() throws Exception {
		PushyConf.init(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("pushy/pushy.properties"));
		System.out.println(PushyConf.IOS_AUTH);
		System.out.println(PushyConf.IOS_PASSWORD);

		exeSvc = new ThreadPoolExecutor(1, Runtime.getRuntime()
				.availableProcessors() + 1, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		eventGroup = new NioEventLoopGroup(1);

		PushManagerConfiguration conf = new PushManagerConfiguration();
		conf.setConcurrentConnectionCount(PushyConf.PUSH_CONN_COUNT);
		PushManager<SimpleApnsPushNotification> pushManager = new PushManager<SimpleApnsPushNotification>(
				getEnvironment(PushyConf.HOST, PushyConf.HOST_PORT,
						PushyConf.FEEDBACK, PushyConf.FEEDBACK_PORT),
				SSLContextUtil.createDefaultSSLContext(
						"/Users/dzh/temp/lech/rentercer.p12",
						PushyConf.IOS_PASSWORD), eventGroup, null, null, conf,
				"PushManager");
		pushy = new PushyServiceImpl();
		pushy.setPushManager(pushManager);

		pushManager.start();
	}

	public static void main(String[] args) throws Exception {
		TestPushy pushy = new TestPushy();
		pushy.init();
		pushy.test();
	}

	@After
	public void stop() {
		pushy.stop();
		eventGroup.shutdownGracefully();
		exeSvc.shutdownNow();
	}

	@Test
	public void test() {
		new Thread() {
			public void run() {
				try {
					System.out.println(new Date());
					for (int i = 0; i < 100; i++) {
						pushApple(token, i + "A", null);
						// Thread.sleep(2);
					}
					System.out.println(new Date());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();

		// new Thread() {
		// public void run() {
		// try {
		// System.out.println(new Date());
		// for (int i = 0; i < 10000; i++) {
		// pushApple("1112222", i + "B", null);
		// Thread.sleep(2);
		// }
		// System.out.println(new Date());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }.start();

		// new Thread() {
		// public void run() {
		// try {
		// System.out.println(new Date());
		// for (int i = 0; i < 100; i++) {
		// pushApple(token, i + "C", null);
		// // Thread.sleep(2);
		// }
		// System.out.println(new Date());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }.start();
		// new Thread() {
		// public void run() {
		// try {
		// System.out.println(new Date());
		// for (int i = 0; i < 100; i++)
		// pushApple(token, i + "D", null);
		// System.out.println(new Date());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }.start();

		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
		}

	}

	public void pushApple(String token, String msg, Integer badge)
			throws Exception {
		ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
		// payloadBuilder.setLaunchImage(Img_Logo);
		payloadBuilder.setAlertBody(msg);
		payloadBuilder.setSoundFileName("default");
		// payloadBuilder.setCategoryName("dzh");
		// payloadBuilder.addCustomProperty("title", "dzh1111");
		// payloadBuilder.setBadgeNumber(badge);
		// Calendar c = Calendar.getInstance();
		// c.add(Calendar.DAY_OF_YEAR, 1);

		String alert = payloadBuilder.buildWithDefaultMaximumLength();
		System.out.println(alert);
		pushy.sendMessage(token, alert, null);
	}

	public static ApnsEnvironment getEnvironment(String host, String port,
			String feedback, String fdPort) {
		return new ApnsEnvironment(host, Integer.parseInt(port), feedback,
				Integer.parseInt(fdPort));
	}

}
