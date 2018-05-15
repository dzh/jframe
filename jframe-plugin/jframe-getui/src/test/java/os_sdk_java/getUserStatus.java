package os_sdk_java;

import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.http.IGtPush;

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