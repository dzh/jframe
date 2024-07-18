package jframe.wxpay;

import jframe.core.util.PropsConf;

import java.io.InputStream;

/**
 * @author dzh
 * @date 2020/8/18 19:39
 */
public class WxpayConf extends PropsConf {

    public static final String CERTNAME = "apiclient_cert.p12";
    public static final String V3_PRIVATE_KEY = "apiclient_key.pem";

    public static final String P_appId = "appId";
    public static final String P_mchId = "mchId";
    public static final String P_apiKey = "apiKey";
    public static final String P_certPath = "certPath";
    public static final String P_notifyUrl = "notifyUrl";
    public static final String P_autoReport = "autoReport";
    public static final String P_useSandbox = "useSandbox";
    public static final String P_signType = "signType";
    //v3
    public static final String P_apiKeyV3 = "apiKeyV3";
    public static final String P_privateKeyPath = "privateKeyPath";
    public static final String P_certSN = "certSN";
    public static final String P_notifyOrderV3 = "notifyOrderV3";
    public static final String P_notifyRefundV3 = "notifyRefundV3";

    public synchronized void init(InputStream is) throws Exception {
        super.init(is);
    }

}
