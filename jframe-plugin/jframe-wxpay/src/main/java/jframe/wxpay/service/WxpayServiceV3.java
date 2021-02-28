package jframe.wxpay.service;

import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.wxpay.WxpayConf;
import jframe.wxpay.WxpayPlugin;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * https://wechatpay-api.gitbook.io/wechatpay-api-v3/
 * https://pay.weixin.qq.com/wiki/doc/apiv3/wxpay/pages/transactions.shtml  接口文档
 * https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient
 * <p>
 * 请求的唯一标示包含在应答的HTTP头Request-ID中
 * <p>
 * User-Agent
 * 使用HTTP客户端默认的User-Agent。
 * 遵循HTTP协议，使用自身系统和应用的名称和版本等信息，组成自己独有的User-Agent。
 * <p>
 * Accept-Language
 * en
 * zh-CN
 * zh-HK
 * zh-TW
 *
 * @author dzh
 * @date 2020/8/18 17:38
 */
@Deprecated
@Injector
public class WxpayServiceV3 {

    static Logger LOG = LoggerFactory.getLogger(WxpayServiceV3.class);

    @InjectPlugin
    static WxpayPlugin plugin;

    static String FILE_WXPAY = "file.wxpay";

    private WxpayConf wxpayConf;

    private Map<String, HttpClient> clients = new HashMap<>();

    @Start
    void start() {
        LOG.info("Start {}", WxpayServiceV3.class.getSimpleName());
        try {
            String file = plugin.getConfig(FILE_WXPAY, plugin.getConfig(Config.APP_CONF) + "/wxpay.properties");
            if (!new File(file).exists()) {
                throw new FileNotFoundException("not found " + file);
            }
            wxpayConf = new WxpayConf();
            wxpayConf.init(file);
            for (String id : wxpayConf.getGroupIds()) {
                HttpClient wxpay = createWxpay(wxpayConf, id);
                clients.put(id, wxpay);
            }
            LOG.info("Start WxpayServiceV3 Successfully!");
        } catch (Exception e) {
            LOG.error("Start WxpayServiceV3 Failed!" + e.getMessage(), e);
        }
    }

    private HttpClient createWxpay(WxpayConf wxpayConf, String id) {
//        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
//                .withMerchant(merchantId, merchantSerialNumber, merchantPrivateKey)
//                .withWechatpay(wechatpayCertificates);
        return null;
    }

    @Stop
    void stop() {
        clients.clear();
        LOG.info("Stop WxpayServiceV3");
    }

}
