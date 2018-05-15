package os_sdk_java;

import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class pushtoAPP {

    static String appId = "";
    static String appkey = "";
    static String master = "";
    static String host = "http://sdk.open.api.igexin.com/apiex.htm";

    public static void main(String[] args) throws Exception {

        IGtPush push = new IGtPush(host, appkey, master);
        // TransmissionTemplate template = TransmissionTemplateDemo();
        LinkTemplate template = linkTemplateDemo();
        AppMessage message = new AppMessage();
        message.setData(template);

        message.setOffline(true);
        message.setOfflineExpireTime(24 * 1000 * 3600);

        List<String> appIdList = new ArrayList<String>();
        List<String> phoneTypeList = new ArrayList<String>();
        List<String> provinceList = new ArrayList<String>();
        List<String> tagList = new ArrayList<String>();

        appIdList.add(appId);
        // phoneTypeList.add("ANDROID");
        // provinceList.add("");
        // tagList.add("脾气");

        message.setAppIdList(appIdList);
        // message.setPhoneTypeList(phoneTypeList);
        // message.setProvinceList(provinceList);
        // message.setTagList(tagList);
        // message.setPushNetWorkType(1);
        // message.setSpeed(1000);
        IPushResult ret = push.pushMessageToApp(message, "任务别名_toApp");
        System.out.println(ret.getResponse().toString());
    }

    public static TransmissionTemplate TransmissionTemplateDemo() throws Exception {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appkey);
        template.setTransmissionType(1);
        template.setTransmissionContent("os-toApp");
        // template.setPushInfo("actionLocKey", 3, "message", "sound", "payload",
        // "locKey", "locArgs", "launchImage");
        return template;
    }

    public static LinkTemplate linkTemplateDemo() throws Exception {
        LinkTemplate template = new LinkTemplate();
        template.setAppId(appId);
        template.setAppkey(appkey);
        template.setTitle("ddd");
        template.setText("ddd");
        template.setLogo("icon.png");
        template.setLogoUrl("");
        template.setIsRing(true);
        template.setIsVibrate(true);
        template.setIsClearable(true);
        template.setUrl("http://www.baidu.com");
        // template.setPushInfo("actionLocKey", 1, "message", "sound",
        // "payload",
        // "locKey", "locArgs", "launchImage",1);
        return template;
    }

}