package com.github.wxpay.sdk;

import java.io.InputStream;

/**
 * @author dzh
 * @date 2020/8/18 19:21
 */
public class JframeWxpayConfig extends WXPayConfig {

    private String appId;

    private String mchId;
    private String apikey;

    private InputStream certStream;

    public static JframeWxpayConfig create(String appId, String mchId, String apikey, InputStream certStream) {
        JframeWxpayConfig conf = new JframeWxpayConfig();
        conf.appId = appId;
        conf.mchId = mchId;
        conf.apikey = apikey;
        conf.certStream = certStream;
        return conf;
    }

    @Override
    String getAppID() {
        return appId;
    }

    @Override
    String getMchID() {
        return mchId;
    }

    @Override
    String getKey() {
        return apikey;
    }

    @Override
    InputStream getCertStream() {
        return certStream;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return null;
    }

//    public boolean shouldAutoReport() {
//        return false;
//    }
}
