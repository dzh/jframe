package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.PushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.APNTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

/**
 * IPushResult pushMessageToSingle(SingleMessage message, Target target)
 * 
 * @author Kevin
 * 
 */
public class PushAPNS {

	static String appId = "";
	static String appKey = "";
	static String masterSecret = "";
	static String dt = "";
	static String url = "http://sdk.open.api.igexin.com/apiex.htm";

	public static void main(String[] args) {
		apnpush();
	}

	public static void apnpush() {
		IGtPush p = new IGtPush(url, appKey, masterSecret);
		APNTemplate template = new APNTemplate();
		template.setPushInfo("", 2, "", "");

		 SingleMessage SingleMessage = new SingleMessage();
		 SingleMessage.setData(template);
		 IPushResult ret = p.pushAPNMessageToSingle(appId, dt, SingleMessage);
		 System.out.println(ret.getResponse());

//		ListMessage lm = new ListMessage();
//		lm.setData(template);
//		String contentId = p.getAPNContentId(appId, lm);
//		List<String> dtl = new ArrayList<String>();
//		dtl.add(dt);
//		System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
//		IPushResult ret = p.pushAPNMessageToList(appId, contentId, dtl);
//		System.out.println(ret.getResponse());
	}
}
