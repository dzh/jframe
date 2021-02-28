package jframe.wxpay.service;

import com.github.wxpay.sdk.JframeWxpayConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.wxpay.WxpayConf;
import jframe.wxpay.WxpayPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * https://pay.weixin.qq.com/wiki/doc/api/index.html
 * https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_1
 *
 * @author dzh
 * @date 2020/8/18 17:40
 */
@Injector
public class WxpayServiceV2 implements WxpayService {

    static Logger LOG = LoggerFactory.getLogger(WxpayServiceV2.class);

    @InjectPlugin
    static WxpayPlugin plugin;

    static String FILE_WXPAY = "file.wxpay";

    //group id -> AlipayClient
    private Map<String, WXPay> clients = new HashMap<>();

    private WxpayConf wxpayConf;

    @Start
    void start() {
        LOG.info("Start WxpayServiceV2");
        try {
            String file = plugin.getConfig(FILE_WXPAY, plugin.getConfig(Config.APP_CONF) + "/wxpay.properties");
            if (!new File(file).exists()) {
                throw new FileNotFoundException("not found " + file);
            }
            wxpayConf = new WxpayConf();
            wxpayConf.init(file);
            for (String id : wxpayConf.getGroupIds()) {
                WXPay wxpay = createWxpay(wxpayConf, id);
                clients.put(id, wxpay);
            }
            LOG.info("Start WxpayServiceV2 Successfully! ids:{}", wxpayConf.getGroupIds());
        } catch (Exception e) {
            LOG.error("Start WxpayServiceV2 Failed!" + e.getMessage(), e);
        }
    }

    @Stop
    void stop() {
        clients.clear();
        LOG.info("Stop WxpayServiceV2");
    }

    private WXPay createWxpay(WxpayConf props, String id) throws Exception {
        byte[] bytes = props.loadCert(id);
        JframeWxpayConfig conf = JframeWxpayConfig.create(props.getConf(id, WxpayConf.P_appId),
                props.getConf(id, WxpayConf.P_mchId), props.getConf(id, WxpayConf.P_apiKey), new ByteArrayInputStream(bytes));
        return new WXPay(conf, props.getConf(id, WxpayConf.P_notifyUrl),
                Boolean.parseBoolean(props.getConf(id, WxpayConf.P_autoReport, "true")),
                Boolean.parseBoolean(props.getConf(id, WxpayConf.P_useSandbox, "false")),
                WXPayConstants.SignType.of(props.getConf(id, WxpayConf.P_signType)));
    }


    @Override
    public String conf(String id, String key) {
        return wxpayConf.getConf(id, key);
    }

    @Override
    public Map<String, String> orderPrepay(String id, Map<String, String> req) throws Exception {
//        req.putIfAbsent("appid", conf(id, WxpayConf.P_appId));
//        req.putIfAbsent("mch_id", conf(id, WxpayConf.P_mchId));
        return clients.get(id).unifiedOrder(req);
    }

    @Override
    public Map<String, String> signPrepay(String id, Map<String, String> req) throws Exception {
        String prepayId = req.get("prepayId");
        if (prepayId == null) {
            return Collections.emptyMap();
        }
        //appId,timeStamp,nonceStr,package,signType
        Map<String, String> data = new HashMap<>();
        data.put("appId", conf(id, WxpayConf.P_appId));
        data.put("timeStamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
        data.put("nonceStr", req.getOrDefault("nonceStr", WXPayUtil.generateNonceStr()));
        data.put("package", prepayId.startsWith("prepay_id") ? prepayId : "prepay_id=" + prepayId);
        WXPayConstants.SignType signType = clients.get(id).signType();
        data.put("signType", signType.typeName());
        String paySign = WXPayUtil.generateSignature(data, conf(id, WxpayConf.P_apiKey), signType);
        //timeStamp,nonceStr,package,signType,paySign
        data.put("paySign", paySign);
        return data;
    }

    @Override
    public Map<String, String> orderClose(String id, Map<String, String> req) throws Exception {
        return clients.get(id).closeOrder(req);
    }

    @Override
    public Map<String, String> orderQuery(String id, Map<String, String> req) throws Exception {
        return clients.get(id).orderQuery(req);
    }

    @Override
    public Map<String, String> processResponseXml(String id, String xmlStr) throws Exception {
        return clients.get(id).processResponseXml(xmlStr);
    }

    @Override
    public Map<String, String> processResponseXmlUnsafe(String xmlStr) throws Exception {
        return WXPayUtil.xmlToMap(xmlStr);
    }

    @Override
    public boolean isResponseSignatureValid(String id, Map<String, String> res) throws Exception {
        return clients.get(id).isResponseSignatureValid(res);
    }

    @Override
    public Map<String, String> refund(String id, Map<String, String> req) throws Exception {
        return clients.get(id).refund(req);
    }

    @Override
    public Map<String, String> refundQuery(String id, Map<String, String> req) throws Exception {
        return clients.get(id).refundQuery(req);
    }
}
