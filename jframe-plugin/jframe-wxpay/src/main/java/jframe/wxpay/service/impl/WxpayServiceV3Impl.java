package jframe.wxpay.service.impl;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.core.util.NonceUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.wxpay.WxpayConf;
import jframe.wxpay.WxpayPlugin;
import jframe.wxpay.service.WxpayServiceV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://wechatpay-api.gitbook.io/wechatpay-api-v3/">V3开发说明</a>
 * <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/wxpay/pages/transactions.shtml">接口文档</a>
 * <a href="https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient">wechatpay-apache-httpclient</a>
 * <a href="https://github.com/wechatpay-apiv3/wechatpay-java?tab=readme-ov-file">v3 java sdk</a>
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
@Injector
public class WxpayServiceV3Impl implements WxpayServiceV3 {

    static Logger LOG = LoggerFactory.getLogger(WxpayServiceV3Impl.class);

    @InjectPlugin
    static WxpayPlugin plugin;

    static String FILE_WXPAY = "file.wxpay";

    private WxpayConf wxpayConf;

    // 使用自动更新平台证书的RSA配置
    // 一个商户号只能初始化一个配置，否则会因为重复的下载任务报错
    private final Map<String, RSAAutoCertificateConfig> payConf = new HashMap<>();//mchId->conf

    @Start
    void start() {
        LOG.info("Start {}", WxpayServiceV3Impl.class.getSimpleName());
        try {
            String file = plugin.getConfig(FILE_WXPAY, plugin.getConfig(jframe.core.conf.Config.APP_CONF) + "/wxpay.properties");
            if (!new File(file).exists()) {
                throw new FileNotFoundException("not found " + file);
            }
            wxpayConf = new WxpayConf();
            wxpayConf.init(file);

            initPayConf(wxpayConf);

            LOG.info("WxpayServiceV3 start successfully!");
        } catch (Exception e) {
            LOG.error("WxpayServiceV3 start failed! {}", e.getMessage(), e);
        }
    }

    private void initPayConf(WxpayConf wxpayConf) {
        for (String id : wxpayConf.getGroupIds()) {
            String mid = wxpayConf.getConf(id, WxpayConf.P_mchId);
            if (payConf.containsKey(mid)) continue;
            String privateKeyPath = wxpayConf.getConf(id, WxpayConf.P_privateKeyPath);
            if (privateKeyPath == null || privateKeyPath.trim().isEmpty()) {
                privateKeyPath = plugin.getConfig(jframe.core.conf.Config.APP_CONF) + "/appclient_key.pem";
            }
            RSAAutoCertificateConfig config = new RSAAutoCertificateConfig.Builder().merchantId(mid).privateKeyFromPath(privateKeyPath).merchantSerialNumber(wxpayConf.getConf(id, WxpayConf.P_certSN)).apiV3Key(wxpayConf.getConf(id, WxpayConf.P_apiKeyV3)).build();
            payConf.put(mid, config);
        }
    }

    private RSAAutoCertificateConfig payConfig(String id) {
        String mid = wxpayConf.getConf(id, WxpayConf.P_mchId);
        return payConf.get(mid);
    }

    @Stop
    void stop() {
        payConf.clear();
        LOG.info("WxpayServiceV3 stopped");
    }

    @Override
    public String conf(String id, String key) {
        return wxpayConf.getConf(id, key);
    }

    private PrepayRequest toPrepayRequest(String id, Map<String, String> req) {
        PrepayRequest request = new PrepayRequest();
        request.setAppid(conf(id, WxpayConf.P_appId));
        request.setMchid(conf(id, WxpayConf.P_mchId));
        request.setDescription(req.getOrDefault("description", req.get("body")));
        request.setOutTradeNo(req.get("out_trade_no"));
        if (req.containsKey("time_expire")) request.setTimeExpire(req.get("time_expire"));
        if (req.containsKey("attach")) request.setAttach(req.get("attach"));
        if (req.containsKey("notify_url")) request.setNotifyUrl(req.get("notify_url"));
        else request.setNotifyUrl(conf(id, WxpayConf.P_notifyOrderV3));
        if (req.containsKey("goods_tag")) request.setGoodsTag(req.get("goods_tag"));
        if (req.containsKey("support_fapiao"))
            request.setSupportFapiao(Boolean.parseBoolean(req.get("support_fapiao")));
        Amount amount = new Amount();
        amount.setTotal(Integer.parseInt(req.get("total_fee")));
        amount.setCurrency(req.getOrDefault("currency", "CNY"));
        request.setAmount(amount);
        Payer payer = new Payer();
        payer.setOpenid(req.get("openid"));
        request.setPayer(payer);
        //
//        request.setDetail();
//        request.setSceneInfo();
//        request.setSettleInfo();
        return request;
    }

    //https://pay.weixin.qq.com/docs/merchant/apis/jsapi-payment/direct-jsons/jsapi-prepay.html
    @Override
    public Map<String, String> orderPrepay(String id, Map<String, String> req) throws Exception {
        JsapiService service = new JsapiService.Builder().config(payConfig(id)).build();
        PrepayResponse response = service.prepay(toPrepayRequest(id, req));
        return Map.of("prepay_id", response.getPrepayId());
    }

    /**
     * @param id  group id
     * @param req Map{prepareId,nonceStr}
     * @return replace with orderPrepay
     */
    @Override
    public Map<String, String> signPrepay(String id, Map<String, String> req) throws Exception {
        Config config = payConfig(id);
//        Signer signer = config.createSigner();

        String appid = conf(id, WxpayConf.P_appId);
        String prepayId = req.get("prepay_id");
        long timestamp = Instant.now().getEpochSecond();
        String nonceStr = NonceUtil.createNonce(32);
        String packageVal = "prepay_id=" + prepayId;
        String message = appid + "\n" + timestamp + "\n" + nonceStr + "\n" + packageVal + "\n";
        String sign = config.createSigner().sign(message).getSign();

        Map<String, String> data = new HashMap<>();
        data.put("appId", appid);
        data.put("timeStamp", String.valueOf(timestamp));
        data.put("nonceStr", nonceStr);
        data.put("package", packageVal);
        data.put("signType", "RSA");
        data.put("paySign", sign);
        return data;
    }

    @Override
    public Map<String, String> orderPrepayAndSign(String id, Map<String, String> req) throws Exception {
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(payConfig(id)).build();
        PrepayRequest request = toPrepayRequest(id, req);
        PrepayWithRequestPaymentResponse prepayRes = service.prepayWithRequestPayment(request);
        //appId,timeStamp,nonceStr,package,signType
        Map<String, String> data = new HashMap<>();
        data.put("appId", conf(id, WxpayConf.P_appId));
        data.put("timeStamp", prepayRes.getTimeStamp());
        data.put("nonceStr", prepayRes.getNonceStr());
        data.put("package", prepayRes.getPackageVal());
        data.put("signType", prepayRes.getSignType());
        data.put("paySign", prepayRes.getPaySign());
        return data;
    }

    //https://pay.weixin.qq.com/docs/merchant/apis/jsapi-payment/close-order.html
    @Override
    public void orderClose(String id, Map<String, String> req) throws Exception {
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(payConfig(id)).build();
        CloseOrderRequest request = new CloseOrderRequest();
        request.setMchid(conf(id, WxpayConf.P_mchId));
        request.setOutTradeNo(req.get("out_trade_no"));
        service.closeOrder(request);
    }

    @Override
    public Transaction orderQuery(String id, Map<String, String> req) throws Exception {
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(payConfig(id)).build();

        Transaction t = null;
        if (req.containsKey("out_trade_no")) {
            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(conf(id, WxpayConf.P_mchId));
            request.setOutTradeNo(req.get("out_trade_no"));
            t = service.queryOrderByOutTradeNo(request);
        } else if (req.containsKey("transaction_id")) {
            QueryOrderByIdRequest request = new QueryOrderByIdRequest();
            request.setMchid(conf(id, WxpayConf.P_mchId));
            request.setTransactionId(req.get("transaction_id"));
            t = service.queryOrderById(request);
        }
//        if (t != null) {
//            return Map.of("appid", t.getAppid(), "out_trade_no", t.getOutTradeNo(), "transaction_id", t.getTransactionId(), "trade_type", t.getTradeType().name(), "trade_state", t.getTradeState().name(), "trade_state_desc", t.getTradeStateDesc(), "attach", t.getAttach());
//        }
        return t;
    }

    /**
     * <a href="https://pay.weixin.qq.com/docs/merchant/apis/mini-program-payment/payment-notice.html">通知API</a>
     * 具体步骤如下：
     * <p>
     * 使用回调通知请求的数据，构建 RequestParam。
     * HTTP 请求体 body。切记使用原始报文，不要用 JSON 对象序列化后的字符串，避免验签的 body 和原文不一致。
     * HTTP 头 Wechatpay-Signature。应答的微信支付签名。
     * HTTP 头 Wechatpay-Serial。微信支付平台证书的序列号，验签必须使用序列号对应的微信支付平台证书。
     * HTTP 头 Wechatpay-Nonce。签名中的随机数。
     * HTTP 头 Wechatpay-Timestamp。签名中的时间戳。
     * HTTP 头 Wechatpay-Signature-Type。签名类型。
     * 初始化 RSAAutoCertificateConfig。微信支付平台证书由 SDK 的自动更新平台能力提供，也可以使用本地证书。
     * 初始化 NotificationParser。
     * 调用 NotificationParser.parse() 验签、解密并将 JSON 转换成具体的通知回调对象。如果验签失败，SDK 会抛出 ValidationException。
     * 接下来可以执行你的业务逻辑了。如果执行成功，你应返回 200 OK 的状态码。如果执行失败，你应返回 4xx 或者 5xx的状态码，例如数据库操作失败建议返回 500 Internal Server Error。
     *
     * @param id      group id
     * @param reqBody request body
     * @param headers request headers contains signature info
     * @return Transaction
     */
    @Override
    public Transaction orderNotify(String id, String reqBody, Map<String, String> headers) throws Exception {
        NotificationConfig config = payConfig(id);
        NotificationParser parser = new NotificationParser(config);

        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder().serialNumber(headers.get("Wechatpay-Serial")).nonce(headers.get("Wechatpay-Nonce")).signature(headers.get("Wechatpay-Signature")).timestamp(headers.get("Wechatpay-Timestamp")).body(reqBody).build();
        return parser.parse(requestParam, Transaction.class);
//        return Map.of("out_trade_no", od.getOutTradeNo(), "success_time", od.getSuccessTime(), "openid", od.getPayer().getOpenid(), "transaction_id", od.getTransactionId(), "result_code", od.getTradeState().name());
    }


    //https://pay.weixin.qq.com/docs/merchant/apis/jsapi-payment/create.html
    @Override
    public Refund refund(String id, Map<String, String> req) throws Exception {
        RefundService service = new RefundService.Builder().config(payConfig(id)).build();
        CreateRequest request = new CreateRequest();
        request.setTransactionId(req.get("transaction_id"));
        request.setOutTradeNo(req.get("out_trade_no"));
        request.setOutRefundNo(req.get("out_refund_no"));
        if (req.containsKey("reason")) request.setReason(req.get("reason"));
        if (req.containsKey("notify_url")) request.setNotifyUrl(req.get("notify_url"));
        else request.setNotifyUrl(conf(id, WxpayConf.P_notifyRefundV3));
//       if(req.containsKey("funds_account"))
//           request.setFundsAccount();
        AmountReq amount = new AmountReq();
        amount.setCurrency(req.getOrDefault("amount_currency", "CNY"));
        amount.setRefund(Long.parseLong(req.get("amount_refund")));
        amount.setTotal(Long.parseLong(req.getOrDefault("amount_total", req.get("refund"))));
        request.setAmount(amount);
        return service.create(request);
    }

    //https://pay.weixin.qq.com/docs/merchant/apis/jsapi-payment/query-by-out-refund-no.html
    @Override
    public Refund refundQuery(String id, Map<String, String> req) throws Exception {
        RefundService service = new RefundService.Builder().config(payConfig(id)).build();
        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        request.setOutRefundNo(req.get("out_refund_no"));
        return service.queryByOutRefundNo(request);
    }

    @Override
    public RefundNotification refundNotify(String id, String reqBody, Map<String, String> headers) throws Exception {
        NotificationConfig config = payConfig(id);
        NotificationParser parser = new NotificationParser(config);
        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder().serialNumber(headers.get("Wechatpay-Serial")).nonce(headers.get("Wechatpay-Nonce")).signature(headers.get("Wechatpay-Signature")).timestamp(headers.get("Wechatpay-Timestamp")).body(reqBody).build();
        return parser.parse(requestParam, RefundNotification.class);
    }

}
