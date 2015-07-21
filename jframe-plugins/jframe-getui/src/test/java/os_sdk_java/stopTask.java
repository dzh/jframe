package os_sdk_java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.http.IGtPush;

public class stopTask {
	static String appId = "";
	static String appkey = "";
	static String master = "";
	static String TaskId="";
	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		stopTask();
	}

	public static void stopTask() throws IOException, InterruptedException {
			IGtPush push = new IGtPush(host, appkey, master);
			boolean result = push.stop(TaskId);
			System.out.println(result);
	}
}