/**
 * 
 */
package jframe.pay.wx.http.client;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jframe.pay.domain.util.IDUtil;
import jframe.pay.wx.domain.WxConfig;
import jframe.pay.wx.domain.WxFields;
import jframe.pay.wx.http.util.MD5Util;

/**
 * https://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=9_1
 * 
 * @author dzh
 * @date Oct 16, 2015 3:57:01 PM
 * @since 1.0
 */
public class WxServiceNew {

    static Logger LOG = LoggerFactory.getLogger(WxServiceNew.class);

    public static boolean genPrePay(Map<String, String> req, Map<String, Object> rsp) {
        try {
            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String nonceStr = genNonceStr();
            String orderNo = genOutTradNo();
            req.put(WxFields.F_orderNo, orderNo);

            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair(WxFields.F_appid, WxConfig.getConf(WxConfig.KEY_APP_ID)));
            packageParams.add(new BasicNameValuePair(WxFields.F_body, req.get(WxFields.F_payDesc)));
            packageParams.add(new BasicNameValuePair(WxFields.F_mch_id, WxConfig.getConf(WxConfig.KEY_PARTNER)));
            packageParams.add(new BasicNameValuePair(WxFields.F_nonce_str, nonceStr));
            packageParams.add(new BasicNameValuePair(WxFields.F_notify_url, WxConfig.getConf(WxConfig.KEY_NOTIFY_URL)));
            packageParams.add(new BasicNameValuePair(WxFields.F_out_trade_no, orderNo));
            packageParams.add(new BasicNameValuePair(WxFields.F_spbill_create_ip, req.get(WxFields.F_remoteIp)));
            packageParams.add(new BasicNameValuePair(WxFields.F_total_fee, req.get(WxFields.F_payAmount)));
            packageParams
                    .add(new BasicNameValuePair(WxFields.F_trade_type, req.getOrDefault(WxFields.F_trade_type, "APP")));

            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));
            String entity = toXml(packageParams);

            // byte[] buf = httpPost(url, entity);
            TenpayHttpClient httpClient = new TenpayHttpClient();
            httpClient.setReqContent(entity);
            if (httpClient.callHttpPost(url, entity)) {
                String content = httpClient.getResContent();
                if (LOG.isDebugEnabled())
                    LOG.debug("genPrePay  url -> {}, req -> {}, rsp -> {}", url, entity, content);

                Map<String, String> xml = decodeXml(content);
                if (xml.get(WxFields.F_return_code).equalsIgnoreCase("SUCCESS")) {
                    // 这些是手机端需要的参数
                    rsp.put(WxFields.F_appid, WxConfig.getConf(WxConfig.KEY_APP_ID));
                    rsp.put(WxFields.F_partnerid, WxConfig.getConf(WxConfig.KEY_PARTNER));
                    rsp.put(WxFields.F_prepayid, xml.get(WxFields.F_prepay_id));
                    rsp.put(WxFields.F_noncestr, xml.get(WxFields.F_nonce_str));
                    rsp.put(WxFields.F_package, "Sign=WXPay");
                    rsp.put(WxFields.F_timestamp, genTimeStamp());
                    rsp.put(WxFields.F_sign, xml.get(WxFields.F_sign));
                    // rsp.put(WxFields.F_trade_type,
                    // xml.get(WxFields.F_trade_type));
                    return true;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    public static Map<String, String> decodeXml(String content) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document source = builder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));

        NodeList nodeList = source.getDocumentElement().getChildNodes();

        Map<String, String> map = new HashMap<>();
        Node node = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            map.put(node.getNodeName(), node.getTextContent());
        }
        return map;
    }

    /**
     * 生成签名
     * 
     * @throws UnsupportedEncodingException
     */
    private static String genPackageSign(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WxConfig.getConf(WxConfig.KEY_APP_SECRET));

        String packageSign = getMessageDigest(sb.toString().getBytes("utf-8")).toUpperCase();

        // String packageSign = MD5Util.MD5Encode(sb.toString(),
        // "").toUpperCase();
        return packageSign;
    }

    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    private static String genOutTradNo() {
        return IDUtil.genOrderNo();
    }

    private static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    private static String genNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "");
        // return
        // MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

}
