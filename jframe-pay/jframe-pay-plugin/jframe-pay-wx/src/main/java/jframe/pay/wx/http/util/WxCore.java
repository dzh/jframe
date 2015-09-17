package jframe.pay.wx.http.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import jframe.pay.wx.domain.WxConfig;

public class WxCore {

    /** = */
    public static final String QSTRING_EQUAL = "=";

    /** & */
    public static final String QSTRING_SPLIT = "&";

    public static Map<String, String> parseQString(String str)
            throws UnsupportedEncodingException {

        Map<String, String> map = new HashMap<String, String>();
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        char curChar;
        String key = null;
        boolean isKey = true;

        for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
            curChar = str.charAt(i);// 取当前字符

            if (curChar == '&') {// 如果读取到&分割符
                putKeyValueToMap(temp, isKey, key, map);
                temp.setLength(0);
                isKey = true;
            } else {
                if (isKey) {// 如果当前生成的是key
                    if (curChar == '=') {// 如果读取到=分隔符
                        key = temp.toString();
                        temp.setLength(0);
                        isKey = false;
                    } else {
                        temp.append(curChar);
                    }
                } else {// 如果当前生成的是value
                    temp.append(curChar);
                }
            }
        }

        putKeyValueToMap(temp, isKey, key, map);

        return map;
    }

    private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
            String key, Map<String, String> map)
            throws UnsupportedEncodingException {
        if (isKey) {
            key = temp.toString();
            if (key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }
            map.put(key, "");
        } else {
            if (key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }
            map.put(key,
                    URLDecoder.decode(temp.toString(),
                            WxConfig.getConf(WxConfig.KEY_CHARSET)));
        }
    }

}
