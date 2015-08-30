/**
 * 
 */
package multigetui;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jframe.getui.ConfField;
import jframe.getui.andriod.MultiGetuiServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

/**
 * @author dzh
 * @date Aug 20, 2015 3:55:41 PM
 * @since 1.0
 */
public class TestMultiGetui {

	MultiGetuiServiceImpl mgs;

	String usr_token = "c66ed5ca8693449c0076db224795ac76";
	String dri_token = "03e6666fe4c2a431bf4689773288fb08";

	static final Date ExpireTime = new Date(72 * 3600 * 1000);

	static String Img_Logo = "push.png";

	@Before
	public void init() throws Exception {
		mgs = (MultiGetuiServiceImpl) MultiGetuiServiceImpl.test(Thread
				.currentThread().getContextClassLoader()
				.getResourceAsStream("multigetui/getui.properties"));
	}

	@After
	public void stop() {
		mgs.stop();
	}

	@Test
	public void testPush() {
		try {
			String token = usr_token;
			pushAndriod("renter", token, "222", "11111111111111");
			pushAndriod("renter", Arrays.asList(token), "333333",
					"444444444444");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pushAndriod(String id, String token, String title, String msg)
			throws Exception {
		mgs.push2Single(id, newTransmissionTemplate(id, title, msg), true,
				ExpireTime.getTime(), token);
	}

	public void pushAndriod(String id, List<String> token, String title,
			String msg) {
		mgs.push2List(id, newTransmissionTemplate(id, title, msg), true,
				ExpireTime.getTime(), token);
	}

	public ITemplate newNotificationTemplate(String id, String title, String msg) {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(mgs.getConf(id, ConfField.KEY_APPID, ""));
		template.setAppkey(mgs.getConf(id, ConfField.KEY_APPKEY, ""));
		template.setTitle(title);
		template.setText(msg);
		// template.setLogo(Img_Logo);
		// template.setLogoUrl("");
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
//		template.setTransmissionType(1);
		template.setTransmissionContent(msg);
		return template;
	}
	public ITemplate newTransmissionTemplate(String id, String title, String msg) {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(mgs.getConf(id, ConfField.KEY_APPID, ""));
		template.setAppkey(mgs.getConf(id, ConfField.KEY_APPKEY, ""));
//		template.setTitle(title);
//		template.setText(msg);
		// template.setLogo(Img_Logo);
		// template.setLogoUrl("");
//		template.setIsRing(true);
//		template.setIsVibrate(true);
//		template.setIsClearable(true);
		template.setTransmissionType(1);
		template.setTransmissionContent(msg);
		return template;
	}

}
