/**
 * 
 */
package jframe.pay.http.ord;

import java.util.Map;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.pay.alipay.service.AlipayService;
import jframe.pay.domain.PayType;
import jframe.pay.domain.dao.OrderAlipay;
import jframe.pay.domain.dao.OrderWx;
import jframe.pay.domain.http.RspCode;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.domain.util.JsonUtil;
import jframe.pay.http.usr.service.CommonService;
import jframe.pay.wx.service.WxpayService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 1, 2015 8:29:42 PM
 * @since 1.0
 */
@Injector
public class OrderService extends CommonService {

    static Logger LOG = LoggerFactory.getLogger(OrderService.class);

    @InjectService(id = "jframe.pay.service.alipay")
    static AlipayService Alipay;

    @InjectService(id = "jframe.pay.service.wxpay")
    static WxpayService Wxpay;

    public void consume(Map<String, String> req, Map<String, Object> rsp)
            throws Exception {
        // check req
        if (HttpUtil.mustReq(req, F_payType, F_payNo, F_transType, F_payAmount,
                F_payDesc).size() > 0) {
            RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
            return;
        }

        int payType = Integer.parseInt(req.get(F_payType));
        if (PayType.A.type == payType) {
            Alipay.pay(req, rsp);
        } else if (PayType.W.type == payType) {
            Wxpay.pay(req, rsp);
        } else {
            RspCode.setRspCode(rsp, RspCode.FAIL_PAYTYPE_NOEXIST);
        }
        // insert
        // if (TransType.isConsume(tt)) {
        // alipay.pay(req, rsp);
        // return;
        // } else if (TransType.ClientPay.code.equals(tt)) {
        // alipay.clientPayBack(req, rsp);
        // return;
        // }
    }

    public void qryod(Map<String, String> req, Map<String, Object> rsp) {
        // check req
        if (HttpUtil.mustReq(req, F_payType, F_payNo).size() > 0) {
            RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
            return;
        }

        int payType = Integer.parseInt(req.get(F_payType));
        if (PayType.A.type == payType) {
            OrderAlipay od = PayDao.selectOrderAlipay(req.get(F_payNo));
            if (od == null) {
                RspCode.setRspCode(rsp, RspCode.FAIL_ORDER_NOT_FOUND);
                return;
            }
            rsp.put(F_od, JsonUtil.toJson(od));
        } else if (PayType.W.type == payType) {
            OrderWx od = PayDao.selectOrderWx(req.get(F_payNo));
            if (od == null) {
                RspCode.setRspCode(rsp, RspCode.FAIL_ORDER_NOT_FOUND);
                return;
            }
            rsp.put(F_od, JsonUtil.toJson(od));
        } else {
            RspCode.setRspCode(rsp, RspCode.FAIL_PAYTYPE_NOEXIST);
        }
    }

    public void aliback(Map<String, String> req, Map<String, Object> rsp)
            throws Exception {
        Alipay.payBack(req, rsp);
    }

    public void wxback(Map<String, String> req, Map<String, Object> rsp)
            throws Exception {
        Wxpay.payBack(req, rsp);
    }

}
