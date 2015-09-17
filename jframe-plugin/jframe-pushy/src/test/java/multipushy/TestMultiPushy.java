/**
 * 
 */
package multipushy;

import java.util.Iterator;
import java.util.Properties;

import jframe.core.conf.Config;
import jframe.core.conf.DefConfig;
import jframe.core.conf.VarHandler;
import jframe.pushy.impl.MultiPushyServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;

/**
 * @author dzh
 * @date Aug 29, 2015 2:06:27 PM
 * @since 1.0
 */
public class TestMultiPushy {

	MultiPushyServiceImpl mgs;

	String usr_token = "70854405ac6b60b64bdc5338a2d8f4a55a683f63e786a872be42454f6731618d";
	String driver_token = "70854405ac6b60b64bdc5338a2d8f4a55a683f63e786a872be42454f6731618d";

	@Before
	public void init() {
		Config conf = new DefConfig();
		conf.addConfig("app.conf", "/Users/dzh/temp/lech");

		mgs = (MultiPushyServiceImpl) MultiPushyServiceImpl.test(
				Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("multipushy/pushy.properties"),
				new VarHandler(conf));
	}

	@Test
	public void push() {
		String id = "renter";
		String id1 = "driver";
		String token = usr_token;
		String token1 = driver_token;

		try {
			mgs.sendMessage(id, token, newPayload("11111", 0, null)
					.buildWithDefaultMaximumLength());
			mgs.sendMessage(id, token, newPayload("22222", 0, null)
					.buildWithDefaultMaximumLength());
			mgs.sendMessage(id, token, newPayload("33333", 0, null)
					.buildWithDefaultMaximumLength());

			mgs.sendMessage(id1, token1, newPayload("44444", 0, null)
					.buildWithDefaultMaximumLength());
			mgs.sendMessage(id1, token1, newPayload("55555", 0, null)
					.buildWithDefaultMaximumLength());
			mgs.sendMessage(id1, token1, newPayload("66666", 0, null)
					.buildWithDefaultMaximumLength());
		} catch (Exception e) {
		}

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
		}
	}

	@After
	public void stop() {
		mgs.stop();
	}

	public ApnsPayloadBuilder newPayload(String msg, Integer badge,
			Properties props) throws Exception {
		ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
		// payloadBuilder.setLaunchImage(Img_Logo);
		payloadBuilder.setAlertBody(msg);
		payloadBuilder.setSoundFileName("push.m4a");
		// payloadBuilder.setBadgeNumber(badge);
		payloadBuilder.setBadgeNumber(0);

		if (props != null) {
			Iterator<Object> iter = props.keySet().iterator();
			while (iter.hasNext()) {
				String key = String.valueOf(iter.next()).trim();
				String val = props.getProperty(key).trim();
				payloadBuilder.addCustomProperty(key, val);
			}
		}
		return payloadBuilder;
	}

}
