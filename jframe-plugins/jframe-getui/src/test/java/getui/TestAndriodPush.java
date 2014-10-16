/**
 * 
 */
package getui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jframe.getui.GetuiConf;
import jframe.getui.andriod.GetuiServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;

/**
 * @author dzh
 * @date Sep 29, 2014 11:51:10 AM
 * @since 1.0
 */
public class TestAndriodPush {

	private GetuiServiceImpl getuiSvc;

	static final Date ExpireTime = new Date(72 * 3600 * 1000);

	static String Img_Logo = "push.png";

	String token = "55a530b956687d37bd205c98041e1377";

	@Before
	public void init() throws IOException {
		GetuiConf.init(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("getui/getui.properties"));
		System.setProperty("gexin.rp.sdk.http.connection.timeout", "30000");
		System.setProperty("gexin.rp.sdk.http.connection.pool.size", "100");
		IGtPush push = new IGtPush(GetuiConf.HOST, GetuiConf.APPKEY,
				GetuiConf.MASTER_SECRET, true);
		getuiSvc = new GetuiServiceImpl();
		getuiSvc.setPush(push);
	}

	@After
	public void stop() {
		getuiSvc.stop();
	}

	@Test
	public void testPush() {
		try {
			pushAndriod(token, "222", "11111111111111");
			pushAndriod(Arrays.asList(token), "333333", "444444444444");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pushAndriod(List<String> token, String title, String msg) {
		getuiSvc.push2List(newNotificationTemplate(title, msg), true,
				ExpireTime.getTime(), token);
	}

	public static ITemplate newNotificationTemplate(String title, String msg) {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(GetuiConf.APPID);
		template.setAppkey(GetuiConf.APPKEY);
		template.setTitle(title);
		template.setText(msg);
		// template.setLogo(Img_Logo);
		// template.setLogoUrl("");
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		template.setTransmissionType(1);
		template.setTransmissionContent(msg);
		return template;
	}

	public void pushAndriod(String token, String title, String msg)
			throws Exception {
		getuiSvc.push2Single(newNotificationTemplate(title, msg), true,
				ExpireTime.getTime(), token);
	}

}
