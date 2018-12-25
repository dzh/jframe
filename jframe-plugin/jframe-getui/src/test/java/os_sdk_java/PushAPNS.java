package os_sdk_java;

import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.APNTemplate;

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
    // static String dt2 = "";
    static String host = "http://sdk.open.api.igexin.com/apiex.htm";

    public static void main(String[] args) throws Exception {
        apnpush();
    }

    public static void apnpush() throws Exception {
        IGtPush p = new IGtPush(host, appKey, masterSecret);
        // *********APN老板本推送方式**************//
        APNTemplate template = new APNTemplate();
        template.setPushInfo("actionLocKey", 2, "body", "", "payload", "locKey", "locArgs", "launchImage");

        // **********APN简单推送********//
        // APNTemplate template = new APNTemplate();
        // APNPayload apnpayload = new APNPayload();
        // com.gexin.rp.sdk.base.payload.APNPayload.SimpleAlertMsg alertMsg =
        // new
        // com.gexin.rp.sdk.base.payload.APNPayload.SimpleAlertMsg("hahahaha");
        // apnpayload.setAlertMsg(alertMsg);
        // apnpayload.setBadge(5);
        // apnpayload.setContentAvailable(1);
        // apnpayload.setCategory("ACTIONABLE");
        //// apnpayload.setSound("test1.wav");
        // template.setAPNInfo(apnpayload);

        // ** APN高级推送**//
        // APNTemplate template = new APNTemplate();
        // APNPayload apnpayload = new APNPayload();
        // apnpayload.setBadge(4);
        // apnpayload.setSound("test2.wav");
        // apnpayload.setContentAvailable(1);
        // apnpayload.setCategory("ACTIONABLE");
        // APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        // alertMsg.setBody("body");
        // alertMsg.setActionLocKey("ActionLockey");
        // alertMsg.setLocKey("LocKey");
        // alertMsg.addLocArg("loc-args");
        // alertMsg.setLaunchImage("launch-image");
        // // IOS8.2以上版本支持
        // alertMsg.setTitle("Title");
        // alertMsg.setTitleLocKey("TitleLocKey");
        // alertMsg.addTitleLocArg("TitleLocArg");
        //
        // apnpayload.setAlertMsg(alertMsg);
        // template.setAPNInfo(apnpayload);

        // 单个用户推送
        // SingleMessage SingleMessage = new SingleMessage();
        // SingleMessage.setData(template);
        // IPushResult ret = p.pushAPNMessageToSingle(appId, dt, SingleMessage);
        // System.out.println(ret.getResponse());

        // 多个用户推送
        ListMessage lm = new ListMessage();
        lm.setData(template);
        String contentId = p.getAPNContentId(appId, lm);
        List<String> dtl = new ArrayList<String>();
        dtl.add(dt);
        // dtl.add(dt2);
        System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
        IPushResult ret = p.pushAPNMessageToList(appId, contentId, dtl);
        System.out.println(ret.getResponse());

    }
}
