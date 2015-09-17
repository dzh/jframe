package jframe.pay.wx.http.util;

import org.json.JSONObject;

public class JsonUtil {

    public static String getJsonValue(String rescontent, String key) {
        JSONObject jsonObject;
        String v = null;
        try {
            jsonObject = new JSONObject(rescontent);
            v = jsonObject.getString(key);
        } catch (Exception e) {
        }
        return v;
    }
}
