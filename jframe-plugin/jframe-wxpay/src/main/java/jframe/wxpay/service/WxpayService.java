package jframe.wxpay.service;

import jframe.core.plugin.annotation.Service;

import java.util.Map;

/**
 * <a href="https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_1">API列表</a>
 * <a href="https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_1">付款到零钱</a>
 *
 * @author dzh
 * @date 2020/8/18 16:45
 */
@Service(clazz = "jframe.wxpay.service.impl.WxpayServiceV2Impl", id = WxpayService.ID)
public interface WxpayService {

    String ID = "jframe.service.wxpay";

    String V2 = "v2";

    String NOTIFY_SUCC = "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    String NOTIFY_FAIL = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";

    default String version() {
        return V2;
    }

    /**
     * @param id  groupid
     * @param key properties key
     * @return properties value
     */
    String conf(String id, String key);

    Map<String, String> orderPrepay(String id, Map<String, String> req) throws Exception;

    /**
     * 签名预支付信息，返回给前端
     * <a href="https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=5">预支付信息</a>
     * <p>
     * Map{appId,timeStamp,nonceStr,package,signType}
     *
     * @param id  group id
     * @param req Map{prepareId,nonceStr}
     * @return Map{timeStamp,nonceStr,package,package,signType,paySign}
     */
    Map<String, String> signPrepay(String id, Map<String, String> req) throws Exception;

    Map<String, String> orderPrepayAndSign(String id, Map<String, String> req) throws Exception;

    Map<String, String> orderClose(String id, Map<String, String> req) throws Exception;

    /**
     * <a href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2">订单查询</a>
     *
     * @param id  group id
     * @param req map{transaction_id,out_trade_no}
     * @return order
     */
    Map<String, String> orderQuery(String id, Map<String, String> req) throws Exception;

    Map<String, String> processResponseXml(String id, String xmlStr) throws Exception;

    @Deprecated
    Map<String, String> processResponseXmlUnsafe(String xmlStr) throws Exception;

    boolean isResponseSignatureValid(String id, Map<String, String> res) throws Exception;

    Map<String, String> refund(String id, Map<String, String> req) throws Exception;

    // 退款通知
    Map<String, String> refundNotify(String id, String reqBody, Map<String, String> headers) throws Exception;

    Map<String, String> refundQuery(String id, Map<String, String> req) throws Exception;

    Map<String, String> promotionTransfers(String id, Map<String, String> reqData) throws Exception;

    Map<String, String> gettransferinfo(String id, Map<String, String> reqData) throws Exception;

    Map<String, String> payBank(String id, Map<String, String> reqData) throws Exception;

    Map<String, String> queryBank(String id, Map<String, String> reqData) throws Exception;

}
