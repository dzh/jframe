package os_sdk_java;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.http.IGtPush;

public class getUserTag {

	static String appId = "";
	static String appkey = "";
	static String master = "";
	static String CID = "";
	static String Alias = "";
	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getUserTags();
	}

	public static void getUserTags() {
		IGtPush push = new IGtPush(host, appkey, master);
		IPushResult result = push.getUserTags(appId, CID);
		System.out.println(result.getResponse().toString());
	}
}
