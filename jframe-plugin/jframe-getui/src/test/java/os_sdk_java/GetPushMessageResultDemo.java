package os_sdk_java;

import java.util.Map;

import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.http.IGtPush;

public class GetPushMessageResultDemo {
    // 您应用的mastersecret
    private static final String MASTERSECRET = "";
    // 您应用的appkey
    private static final String APPKEY = "";
    // 要查询的taskId
    private static final String APPID = "";

    private static final String TASKID = "";

    static String host = "http://sdk.open.api.igexin.com/apiex.htm";

    public static void main(String[] args) {
        // System.setProperty("gexin_get_domain_flag","true");
        // System.setProperty("gexin_default_domainurl","http://172.16.13.135:80/apiex.htm");
        IGtPush push = new IGtPush(host, APPKEY, MASTERSECRET);
        // System.out.println(push.getPushResult(TASKID).getResponse());
        // IPushResult result=push.getPushResult(TASKID);
        // String msgTotal =result.getResponse().get("msgTotal").toString();
        // String clickNum=result.getResponse().get("clickNum").toString();
        // String msgProcess =result.getResponse().get("msgProcess").toString();
        // System.out.println("总下发数:"+msgTotal+"|点击数:"+clickNum+"|下发的消息总数:"+msgProcess);

        IQueryResult result = push.queryAppUserDataByDate(APPID, "20150525");
        Map<String, Object> data = (Map<String, Object>) result.getResponse().get("data");
        System.out.println(result.getResponse().toString());

        System.out.println("新用户注册总数:" + data.get("newRegistCount"));
        System.out.println("用户注册总数:" + data.get("registTotalCount"));
        System.out.println("活跃用户数:" + data.get("activeCount"));
        System.out.println("在线用户数:" + data.get("onlineCount"));

        // IQueryResult result1 = push.queryAppPushDataByDate(APPID, "20150525");
        // Map<String, Object> data1 = (Map<String, Object>)result1.getResponse().get("data");
        // System.out.println(data);
        // System.out.println("发送总数:"+data1.get("sendCount"));
        // System.out.println("在线下发数:"+data1.get("sendOnlineCount"));
        // System.out.println("接收总数:"+data1.get("receiveCount"));
        // System.out.println("展示总数:"+data1.get("showCount"));
        // System.out.println("点击总数:"+data1.get("clickCount"));

    }

}
