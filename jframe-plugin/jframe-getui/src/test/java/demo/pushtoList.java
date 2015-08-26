package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.NotyPopLoadTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class pushtoList {

	static String appId = "";
	static String appkey = "";
	static String master = "";
	static String cids = "e83136a545884736e59e6a613a49ab04";

	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	public static void main(String[] args) throws Exception {
		System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");

		final IGtPush push = new IGtPush(host, appkey, master);
		push.connect();
		LinkTemplate template = linkTemplateDemo();
		// TransmissionTemplate template = TransmissionTemplateDemo();
		// LinkTemplate template = linkTemplateDemo();
		// NotificationTemplate template = NotificationTemplateDemo();
		// NotyPopLoadTemplate template = NotyPopLoadTemplateDemo();

		ListMessage message = new ListMessage();
		
		message.setData(template);

		message.setOffline(true);
		message.setOfflineExpireTime(72 * 1000 * 3600);

		List<Target> targets = new ArrayList<Target>();
		Target target1 = new Target();
		target1.setAppId(appId);
		target1.setClientId(cids);
		targets.add(target1);
		String taskId = push.getContentId(message);
		IPushResult ret = push.pushMessageToList(taskId, targets);
		System.out.println(ret.getResponse().toString());
	}

	public static TransmissionTemplate TransmissionTemplateDemo()
			throws Exception {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTransmissionType(1);
		template.setTransmissionContent("测试透传推送结果，收到的群里回下消息");
		template.setPushInfo("actionLocKey", 2, "message", "sound", "payload",
				"locKey", "locArgs", "launchImage");
		return template;
	}

	public static LinkTemplate linkTemplateDemo() throws Exception {
		LinkTemplate template = new LinkTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTitle("标题");
		template.setText("内容");
		template.setLogo("icon.png");
		template.setLogoUrl("");
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		template.setUrl("http://www.baidu.com");
		template.setPushInfo("actionLocKey", 1, "message", "sound", "payload",
				"locKey", "locArgs", "launchImage");
		return template;
	}

	public static NotificationTemplate NotificationTemplateDemo()
			throws Exception {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTitle("title");
		template.setText("text");
		template.setLogo("icon.png");
		template.setLogoUrl("");
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		template.setTransmissionType(1);
		template.setTransmissionContent("text");
		template.setPushInfo("actionLocKey", 2, "message", "sound", "payload",
				"locKey", "locArgs", "launchImage");
		return template;
	}

	public static NotyPopLoadTemplate NotyPopLoadTemplateDemo() {
		NotyPopLoadTemplate template = new NotyPopLoadTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setNotyTitle("title");
		template.setNotyContent("text");
		template.setNotyIcon("icon.png");
		template.setBelled(true);
		template.setVibrationed(true);
		template.setCleared(true);

		template.setPopTitle("pop");
		template.setPopContent("popcontent");
		template.setPopImage("http://www-igexin.qiniudn.com/wp-content/uploads/2013/08/logo_getui1.png");
		template.setPopButton1("Button1");
		template.setPopButton2("Button2");

		template.setLoadTitle("poptitle");
		template.setLoadIcon("file://icon.png");
		template.setLoadUrl("http://wap.igexin.com/android_download/Gexin_android_2.0.apk");
		return template;
	}
}