package os_sdk_java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Templates;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.NotyPopLoadTemplate;
import com.gexin.rp.sdk.template.PopupTransmissionTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class getUserStatus {
	static String appId = "";
	static String appkey = "";
	static String master = "";
	static String CID = "";
	static String Alias = "";

	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	public static void main(String[] args) throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);
		push.connect();

		getUserStatus();
	}

	public static void getUserStatus() {
		IGtPush push = new IGtPush(host, appkey, master);
		IQueryResult abc = push.getClientIdStatus(appId, CID);
		System.out.println(abc.getResponse());
	}
}