package demo;

import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.NotyPopLoadTemplate;
import com.gexin.rp.sdk.template.PopupTransmissionTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class pushtoSingle {
	static String appId = "";
	static String appkey = "";
	static String master = "";
	static String CID = "";

	// static String host = "http://192.168.10.61:8006/apiex.htm";
	// static String host = "http://edj-s.open.api.igexin.com:8006/apiex.htm";
	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	public static void main(String[] args) throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);
		push.connect();

		for (int i = 0; i < 1; i++) {
			// TransmissionTemplate template = TransmissionTemplateDemo();
			// LinkTemplate template = linkTemplateDemo();
			// NotificationTemplate template = NotificationTemplateDemo();
			// NotyPopLoadTemplate template =NotyPopLoadTemplateDemo();
			PopupTransmissionTemplate template = PopupTransmissionTemplateDemo();

			SingleMessage message = new SingleMessage();
			message.setOffline(true);
			message.setOfflineExpireTime(72 * 3600 * 1000);
			message.setData(template);

			List<Target> targets = new ArrayList<Target>();
			Target target1 = new Target();
			Target target2 = new Target();

			target1.setAppId(appId);
			target1.setClientId(CID);

			IPushResult ret = push.pushMessageToSingle(message, target1);
			System.out.println(ret.getResponse().toString());
			// System.out.println(ret.getResponse().get("status").equals("successed_offline"));
		}
	}

	public static PopupTransmissionTemplate PopupTransmissionTemplateDemo() {
		PopupTransmissionTemplate template = new PopupTransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setText("");
		template.setTitle("");
		template.setImg("");
		template.setConfirmButtonText("");
		template.setCancelButtonText("");
		template.setTransmissionContent("111");
		template.setTransmissionType(1);

		return template;
	}

	public static TransmissionTemplate TransmissionTemplateDemo()
			throws Exception {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTransmissionType(1);
		template.setTransmissionContent("");
		template.setPushInfo("actionLocKey", 2, "message", "sound", "payload",
				"locKey", "locArgs", "launchImage");
		return template;
	}

	public static LinkTemplate linkTemplateDemo() throws Exception {
		LinkTemplate template = new LinkTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTitle("");
		template.setText("");
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
		template.setTitle("内容");
		template.setText("标题");
		template.setLogo("icon.png");
		template.setLogoUrl("");
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		template.setTransmissionType(1);
		template.setTransmissionContent("dddd");
		template.setPushInfo("actionLocKey", 2, "message", "sound", "payload",
				"locKey", "locArgs", "launchImage");
		return template;
	}

	public static NotyPopLoadTemplate NotyPopLoadTemplateDemo() {
		NotyPopLoadTemplate template = new NotyPopLoadTemplate();
		// 填写appid与appkey
		template.setAppId(appId);
		template.setAppkey(appkey);
		// 填写通知标题和内容
		template.setNotyTitle("标题");
		template.setNotyContent("内容");
		// 填写图标文件名称
		template.setNotyIcon("icon.png");
		// 设置响铃，震动，与可清除
		template.setBelled(false);
		template.setVibrationed(false);
		template.setCleared(true);

		// 设置弹框标题与内容
		template.setPopTitle("弹框标题");
		template.setPopContent("弹框内容");
		// 设置弹框图片
		template.setPopImage("http://www-igexin.qiniudn.com/wp-content/uploads/2013/08/logo_getui1.png");
		template.setPopButton1("打开");
		template.setPopButton2("取消");

		// 设置下载标题，图片与下载地址
		template.setLoadTitle("下载标题");
		template.setLoadIcon("file://icon.png");
		template.setLoadUrl("http://wap.igexin.com/android_download/Gexin_android_2.0.apk");
		return template;
	}
}