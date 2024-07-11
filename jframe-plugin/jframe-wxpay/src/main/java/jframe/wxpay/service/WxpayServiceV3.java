package jframe.wxpay.service;

import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.RefundNotification;
import jframe.core.plugin.annotation.Service;

import java.util.Map;

/**
 * @author dzh
 * @date 2024/7/3 09:49
 */
@Service(clazz = "jframe.wxpay.service.impl.WxpayServiceV3Impl", id = WxpayServiceV3.ID)
public interface WxpayServiceV3 {

    String ID = "jframe.service.wxpayv3";

    String V3 = "v3";

    default String version() {
        return V3;
    }

    String conf(String id, String key);

    Map<String, String> orderPrepay(String id, Map<String, String> req) throws Exception;

    Map<String, String> signPrepay(String id, Map<String, String> req) throws Exception;

    Map<String, String> orderPrepayAndSign(String id, Map<String, String> req) throws Exception;

    void orderClose(String id, Map<String, String> req) throws Exception;

    Transaction orderQuery(String id, Map<String, String> req) throws Exception;

    Transaction orderNotify(String id, String reqBody, Map<String, String> headers) throws Exception;

    Refund refund(String id, Map<String, String> req) throws Exception;

    Refund refundQuery(String id, Map<String, String> req) throws Exception;

    RefundNotification refundNotify(String id, String reqBody, Map<String, String> headers) throws Exception;
}
