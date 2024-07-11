package jframe.wxpay.service.impl;

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
import jframe.wxpay.service.WxpayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://pay.weixin.qq.com/wiki/doc/api/index.html">开发文档首页</a>
 *
 * @author dzh
 * @date 2020/8/18 17:40
 */
@Injector
public class WxpayServiceV2Impl implements WxpayService {

    static Logger LOG = LoggerFactory.getLogger(WxpayServiceV2Impl.class);

    @InjectPlugin
    static WxpayPlugin plugin;

    static String FILE_WXPAY = "file.wxpay";

    //group id -> AlipayClient
    private final Map<String, WXPay> clients = new HashMap<>();

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
                LOG.info("createWxpay id {}", id);
            }
        } catch (Exception e) {
            LOG.error("Start WxpayServiceV2 Failed!" + e.getMessage(), e);
        }
    }

    @Stop
    void stop() {
        clients.clear();
        LOG.info("Stop WxpayServiceV2");
    }

    /**
     * @param id groupid
     * @return cert bytes
     */
    byte[] loadCert(WxpayConf props, String id) throws IOException {
        String certPath = props.getConf(id, WxpayConf.P_certPath);
        File file = new File(certPath);
        if (!file.exists()) {
            file = Paths.get(plugin.getConfig(Config.APP_CONF), WxpayConf.CERTNAME).toFile();
        }
        if (!file.exists()) {
            throw new FileNotFoundException(WxpayConf.CERTNAME);
        }
        try (InputStream certStream = Files.newInputStream(file.toPath())) {
            byte[] certData = new byte[(int) file.length()];
            long size = certStream.read(certData);
            LOG.info("read {} {}", size, WxpayConf.CERTNAME);
            return certData;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new IOException(e.getMessage(), e.getCause());
        }
    }

    private WXPay createWxpay(WxpayConf props, String id) throws Exception {
        byte[] bytes = loadCert(props, id);
        JframeWxpayConfig conf = JframeWxpayConfig.create(props.getConf(id, WxpayConf.P_appId), props.getConf(id, WxpayConf.P_mchId), props.getConf(id, WxpayConf.P_apiKey), new ByteArrayInputStream(bytes));
        return new WXPay(conf, props.getConf(id, WxpayConf.P_notifyUrl), Boolean.parseBoolean(props.getConf(id, WxpayConf.P_autoReport, "true")), Boolean.parseBoolean(props.getConf(id, WxpayConf.P_useSandbox, "false")), WXPayConstants.SignType.of(props.getConf(id, WxpayConf.P_signType)));
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
    public Map<String, String> orderPrepayAndSign(String id, Map<String, String> req) throws Exception {
        return signPrepay(id, orderPrepay(id, req));
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
    public Map<String, String> refundNotify(String id, String reqBody, Map<String, String> headers) throws Exception {
        return Map.of();//todo
    }

    @Override
    public Map<String, String> refundQuery(String id, Map<String, String> req) throws Exception {
        return clients.get(id).refundQuery(req);
    }

    @Override
    public Map<String, String> promotionTransfers(String id, Map<String, String> reqData) throws Exception {
        return clients.get(id).promotionTransfers(reqData);
    }

    @Override
    public Map<String, String> gettransferinfo(String id, Map<String, String> reqData) throws Exception {
        return clients.get(id).gettransferinfo(reqData);
    }

    @Override
    public Map<String, String> payBank(String id, Map<String, String> reqData) throws Exception {
        return clients.get(id).payBank(reqData);
    }

    @Override
    public Map<String, String> queryBank(String id, Map<String, String> reqData) throws Exception {
        return clients.get(id).queryBank(reqData);
    }
}
