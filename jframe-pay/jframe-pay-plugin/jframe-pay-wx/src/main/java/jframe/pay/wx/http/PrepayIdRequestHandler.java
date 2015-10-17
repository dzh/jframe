package jframe.pay.wx.http;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONException;

import jframe.pay.wx.http.client.TenpayHttpClient;
import jframe.pay.wx.http.util.ConstantUtil;
import jframe.pay.wx.http.util.JsonUtil;
import jframe.pay.wx.http.util.Sha1Util;

public class PrepayIdRequestHandler extends RequestHandler {

    static Logger LOG = LoggerFactory.getLogger(PrepayIdRequestHandler.class);

    public PrepayIdRequestHandler() {
        super();
    }

    /**
     * 创建签名SHA1
     * 
     * @param signParams
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public String createSHA1Sign() {
        StringBuilder sb = new StringBuilder();
        Set es = super.getAllParameters().entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k + "=" + v + "&");
        }
        String params = sb.substring(0, sb.lastIndexOf("&"));
        String appsign = Sha1Util.getSha1(params);
        this.setDebugInfo(this.getDebugInfo() + "\r\n" + "sha1 sb:" + params);
        this.setDebugInfo(this.getDebugInfo() + "\r\n" + "app sign:" + appsign);
        return appsign;
    }

    // 提交预支付
    @SuppressWarnings("rawtypes")
    public String sendPrepay() throws JSONException {
        String prepayid = "";
        StringBuilder sb = new StringBuilder("{");
        Set es = super.getAllParameters().entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"appkey".equals(k)) {
                sb.append("\"" + k + "\":\"" + v + "\",");
            }
        }
        String params = sb.substring(0, sb.lastIndexOf(","));
        params += "}";

        String requestUrl = super.getGateUrl();
        this.setDebugInfo(this.getDebugInfo() + "\r\n" + "requestUrl:" + requestUrl);
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setReqContent(requestUrl);
        String resContent = "";
        this.setDebugInfo(this.getDebugInfo() + "\r\n" + "post data:" + params);
        if (httpClient.callHttpPost(requestUrl, params)) {
            resContent = httpClient.getResContent();
            if (2 == resContent.indexOf("prepayid")) {
                prepayid = JsonUtil.getJsonValue(resContent, "prepayid");
            }
            this.setDebugInfo(this.getDebugInfo() + "\r\n" + "resContent:" + resContent);
        }
        if (LOG.isDebugEnabled())
            LOG.debug("sendPrepay debugInfo -> {}", this.getDebugInfo());
        return prepayid;
    }

    // 判断access_token是否失效
    @SuppressWarnings("rawtypes")
    public String sendAccessToken() {
        String accesstoken = "";
        StringBuilder sb = new StringBuilder("{");
        Set es = super.getAllParameters().entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"appkey".equals(k)) {
                sb.append("\"" + k + "\":\"" + v + "\",");
            }
        }
        String params = sb.substring(0, sb.lastIndexOf(","));
        params += "}";

        String requestUrl = super.getGateUrl();
        // this.setDebugInfo(this.getDebugInfo() + "\r\n" + "requestUrl:"
        // + requestUrl);
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setReqContent(requestUrl);
        String resContent = "";
        // this.setDebugInfo(this.getDebugInfo() + "\r\n" + "post data:" +
        // params);
        if (httpClient.callHttpPost(requestUrl, params)) {
            resContent = httpClient.getResContent();
            if (2 == resContent.indexOf(ConstantUtil.ERRORCODE)) {
                accesstoken = resContent.substring(11, 16);// 获取对应的errcode的值
            }
        }
        return accesstoken;
    }
}
