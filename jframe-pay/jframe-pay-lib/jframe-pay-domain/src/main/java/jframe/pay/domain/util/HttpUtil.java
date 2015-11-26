/**
 * 
 */
package jframe.pay.domain.util;

import static java.util.stream.Collectors.toList;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author dzh
 * @date Jul 13, 2015 5:44:13 PM
 * @since 1.0
 */
public class HttpUtil {

    /**
     * If req not contains all mustParas , then return emptyList contains empty
     * parameter
     * 
     * @param req
     *            req parameters
     * @param mustParas
     *            parameter must not null
     * @return list contains empty parameters
     */
    public static List<String> mustReq(Map<String, String> req, String... mustParas) {
        if (req == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(mustParas).filter(key -> !(req.containsKey(key) && ObjectUtil.notEmpty(req.get(key))))
                .collect(toList());
    }

    public static boolean anyReq(Map<String, String> req, String... paras) {
        if (req == null || req.isEmpty()) {
            return false;
        }

        return Arrays.stream(paras).anyMatch(key -> req.containsKey(key) && ObjectUtil.notEmpty(req.get(key)));
    }

    public static boolean anyEq(String val, String... vals) {
        if (ObjectUtil.isEmpty(val)) {
            return false;
        }

        return Arrays.stream(vals).anyMatch(v -> v.equals(vals));
    }

    public final static String format(Map<String, String> paras, String charset) {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>(paras.keySet().size());
        for (Map.Entry<String, String> param : paras.entrySet()) {
            NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
            paramList.add(pair);
        }
        return URLEncodedUtils.format(paramList, charset);
    }

    /**
     * 目前只支持单值情况， 不支持多值和编码
     * 
     * @param content
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> parseHttpParas(String content) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        int len = content.length();

        StringBuilder buf = new StringBuilder(16);
        String key = null;
        for (int i = 0; i < len; ++i) {
            char ch = content.charAt(i);

            if (ch == '=') {
                key = buf.toString();
                buf.setLength(0);
                continue;
            }
            if (ch == '&') {
                map.put(key, buf.toString());
                buf.setLength(0);
                continue;
            }
            buf.append(ch);
        }
        map.put(key, buf.toString());
        return map;
    }

    public static Map<String, String> parseHttpParas(String content, String fromCharset, String toCharset)
            throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        int len = content.length();

        StringBuilder buf = new StringBuilder(16);
        String key = null;
        for (int i = 0; i < len; ++i) {
            char ch = content.charAt(i);

            if (ch == '=') {
                key = buf.toString();
                buf.setLength(0);
                continue;
            }
            if (ch == '&') {
                map.put(key, new String(buf.toString().getBytes(fromCharset), toCharset));
                buf.setLength(0);
                continue;
            }
            buf.append(ch);
        }
        map.put(key, buf.toString());
        return map;
    }

}
