package jframe.pay.wx.http;

import jframe.pay.wx.domain.WxConfig;
import jframe.pay.wx.http.client.TenpayHttpClient;
import jframe.pay.wx.http.util.ConstantUtil;
import jframe.pay.wx.http.util.JsonUtil;
import jframe.pay.wx.http.util.WxUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessTokenRequestHandler extends RequestHandler {

    static Logger LOG = LoggerFactory
            .getLogger(AccessTokenRequestHandler.class);

    public AccessTokenRequestHandler() {
        super();
    }

    private static String access_token = "";

    /**
     * 获取凭证access_token
     * 
     * @return
     */
    public static String getAccessToken() {
        if ("".equals(access_token)) {// 如果为空直接获取
            return getTokenReal();
        }

        if (tokenIsExpire(access_token)) {// 如果过期重新获取
            return getTokenReal();
        }
        return access_token;
    }

    /**
     * 实际获取access_token的方法
     * 
     * @return
     */
    public static String getTokenReal() {
        String requestUrl = WxConfig.getConf(WxConfig.KEY_TOKEN_URL)
                + "?grant_type=" + WxConfig.getConf(WxConfig.KEY_GRANT_TYPE)
                + "&appid=" + WxConfig.getConf(WxConfig.KEY_APP_ID)
                + "&secret=" + WxConfig.getConf(WxConfig.KEY_APP_SECRET);
        String resContent = "";
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setMethod("GET");
        httpClient.setReqContent(requestUrl);
        if (httpClient.call()) {
            resContent = httpClient.getResContent();
            if (resContent.indexOf(ConstantUtil.ACCESS_TOKEN) > 0) {
                access_token = JsonUtil.getJsonValue(resContent,
                        ConstantUtil.ACCESS_TOKEN);
            } else {
                LOG.info("获取access_token值返回错误！！！");
            }
        } else {
            LOG.error("后台调用通信失败 ResponseCode->{}, ErrInfo->{},requestUrl->{}",
                    httpClient.getResponseCode(), httpClient.getErrInfo(),
                    requestUrl);
            // 有可能因为网络原因，请求已经处理，但未收到应答。
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("requestUrl -> {}", requestUrl);
        }

        return access_token;
    }

    /**
     * 判断传递过来的参数access_token是否过期 TODO wxReqHandler.setParameter("package",
     * ConstantUtil.packageValue); 这个值哪里取
     * 
     * @param access_token
     * @return
     */
    @Deprecated
    private static boolean tokenIsExpire(String access_token) {
        boolean flag = false;
        PrepayIdRequestHandler wxReqHandler = new PrepayIdRequestHandler();
        wxReqHandler.setParameter("appid",
                WxConfig.getConf(WxConfig.KEY_APP_ID));
        wxReqHandler.setParameter("appkey",
                WxConfig.getConf(WxConfig.KEY_APP_KEY));
        wxReqHandler.setParameter("noncestr", WxUtil.getNonceStr());
        // wxReqHandler.setParameter("package", ConstantUtil.packageValue);
        wxReqHandler.setParameter("timestamp", WxUtil.getTimeStamp());
        wxReqHandler.setParameter("traceid", "");

        // 生成支付签名
        String sign = wxReqHandler.createSHA1Sign();
        wxReqHandler.setParameter("app_signature", sign);
        wxReqHandler.setParameter("sign_method",
                WxConfig.getConf(WxConfig.KEY_SIGN_METHOD));
        String gateUrl = ConstantUtil.GATEURL + access_token;
        wxReqHandler.setGateUrl(gateUrl);

        // 发送请求
        String accesstoken = wxReqHandler.sendAccessToken();
        if (ConstantUtil.EXPIRE_ERRCODE.equals(accesstoken)
                || ConstantUtil.FAIL_ERRCODE.equals(accesstoken))
            flag = true;
        return flag;
    }

}
