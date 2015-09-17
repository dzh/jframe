package jframe.pay.wx.http.client;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import jframe.pay.domain.DevType;
import jframe.pay.domain.PayException;
import jframe.pay.domain.dao.OrderWx;
import jframe.pay.wx.domain.WxConfig;
import jframe.pay.wx.domain.WxFields;
import jframe.pay.wx.http.ClientRequestHandler;
import jframe.pay.wx.http.PackageRequestHandler;
import jframe.pay.wx.http.PrepayIdRequestHandler;
import jframe.pay.wx.http.RequestHandler;
import jframe.pay.wx.http.ResponseHandler;
import jframe.pay.wx.http.util.WxCore;
import jframe.pay.wx.http.util.WxUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 微信接口方法
 * 
 * @author dzh
 * @date Sep 25, 2014 8:38:27 PM
 * @since 1.0
 */
public class WxService implements WxFields {

    static Logger LOG = LoggerFactory.getLogger(WxService.class);

    /**
     * 生成预支付订单
     * 
     * @param req
     * @return
     */
    public static boolean genPrePay(Map<String, String> req,
            Map<String, Object> rsp) throws Exception {
        String out_trade_no = WxUtil.genTradeNo();
        // 设置package订单参数
        PackageRequestHandler packageReqHandler = new PackageRequestHandler(); // 生成package的请求类
        packageReqHandler.setKey(WxConfig.getConf(WxConfig.KEY_PARTNER_KEY));
        packageReqHandler.setParameter(WxFields.F_bank_type, "WX");// 银行渠道
        packageReqHandler.setParameter(WxFields.F_body,
                req.get(WxFields.F_payDesc)); // 商品描述
        packageReqHandler.setParameter(WxFields.F_notify_url,
                WxConfig.getConf(WxConfig.KEY_NOTIFY_URL)); // 接收财付通通知的URL
        packageReqHandler.setParameter(WxFields.F_partner,
                WxConfig.getConf(WxConfig.KEY_PARTNER)); // 商户号
        packageReqHandler.setParameter(WxFields.F_out_trade_no, out_trade_no); // 商家订单号
        packageReqHandler.setParameter(WxFields.F_total_fee,
                req.get(WxFields.F_payAmount)); // 商品金额,以分为单位
        packageReqHandler.setParameter(WxFields.F_spbill_create_ip,
                req.get(WxFields.F_remoteIp)); // 订单生成的机器IP，指用户浏览器端IP
        packageReqHandler.setParameter(WxFields.F_fee_type, "1"); // 币种，1人民币 66
        packageReqHandler.setParameter(WxFields.F_input_charset,
                WxConfig.getConf(WxConfig.KEY_CHARSET)); // 字符编码
        // 获取package包
        String packageValue = packageReqHandler.getRequestURL();

        // 设置获取prepayid支付参数
        String noncestr = WxUtil.getNonceStr();
        String timestamp = WxUtil.getTimeStamp();
        // 获取prepayid的请求类
        PrepayIdRequestHandler prepayReqHandler = new PrepayIdRequestHandler();
        prepayReqHandler.setParameter(WxFields.F_appid,
                WxConfig.getConf(WxConfig.KEY_APP_ID));
        prepayReqHandler.setParameter(WxFields.F_appkey,
                WxConfig.getConf(WxConfig.KEY_APP_KEY));
        prepayReqHandler.setParameter(WxFields.F_noncestr, noncestr);
        prepayReqHandler.setParameter(WxFields.F_package, packageValue);
        prepayReqHandler.setParameter(WxFields.F_timestamp, timestamp);
        prepayReqHandler.setParameter(WxFields.F_traceid, "");

        // 生成获取预支付签名
        String sign = prepayReqHandler.createSHA1Sign();
        // 增加非参与签名的额外参数
        prepayReqHandler.setParameter(WxFields.F_app_signature, sign);
        prepayReqHandler.setParameter(WxFields.F_sign_method,
                WxConfig.getConf(WxConfig.KEY_SIGN_METHOD));
        String gateUrl = WxConfig.getConf(WxConfig.KEY_GATE_URL)
                + "?access_token=" + req.get(WxFields.F_token);
        prepayReqHandler.setGateUrl(gateUrl);

        // 获取prepayId
        String prepayid = prepayReqHandler.sendPrepay();
        if (null != prepayid && !"".equals(prepayid)) {
            // packageValue = "Sign=" + packageValue; 返回客户端支付参数的请求类
            ClientRequestHandler clientHandler = new ClientRequestHandler();
            clientHandler.setParameter(WxFields.F_appid,
                    WxConfig.getConf(WxConfig.KEY_APP_ID));
            clientHandler.setParameter(WxFields.F_appkey,
                    WxConfig.getConf(WxConfig.KEY_APP_KEY));
            clientHandler.setParameter(WxFields.F_noncestr, noncestr);
            if (req.get(WxFields.F_devType).equals(DevType.ANDRIOD.toString())) {
                clientHandler.setParameter(WxFields.F_package, "Sign="
                        + packageValue);
            } else if (req.get(WxFields.F_devType).equals(
                    DevType.IOS.toString())) {
                clientHandler.setParameter(WxFields.F_package, "Sign=WXPay");
            } else {
                throw new PayException("Invalid devType -> "
                        + req.get(WxFields.F_devType) + "when genPrePay");
            }
            clientHandler.setParameter(WxFields.F_partnerid,
                    WxConfig.getConf(WxConfig.KEY_PARTNER));
            clientHandler.setParameter(WxFields.F_prepayid, prepayid);
            clientHandler.setParameter(WxFields.F_timestamp, timestamp);
            sign = clientHandler.createSHA1Sign();

            // TODO 从安全角度是否存手机本地
            // 这些是手机端需要的参数
            rsp.put(WxFields.F_appid, WxConfig.getConf(WxConfig.KEY_APP_ID));
            rsp.put(WxFields.F_partnerid,
                    WxConfig.getConf(WxConfig.KEY_PARTNER));
            rsp.put(WxFields.F_prepayid, prepayid);
            rsp.put(WxFields.F_noncestr, noncestr);
            rsp.put(WxFields.F_timestamp, timestamp);
            rsp.put(WxFields.F_packageValue, packageValue);
            rsp.put(WxFields.F_payDesc, req.get(WxFields.F_payDesc));
            rsp.put(WxFields.F_sign, sign);

            return true;
        }
        return false;
    }

    public static boolean backPay(Map<String, String> req,
            Map<String, Object> rsp) {
        // 创建支付应答对象
        ResponseHandler resHandler = new ResponseHandler(req);
        resHandler.setKey(WxConfig.getConf(WxConfig.KEY_PARTNER_KEY));
        // 判断签名
        if (resHandler.isTenpaySign()) {
            // 通知id
            String notify_id = resHandler.getParameter(WxFields.F_notify_id);
            // 创建请求对象
            RequestHandler queryReq = new RequestHandler();
            // 通信对象
            TenpayHttpClient httpClient = new TenpayHttpClient();
            // 应答对象
            ClientResponseHandler queryRes = new ClientResponseHandler();

            // 通过通知ID查询，确保通知来至财付通
            queryReq.init();
            queryReq.setKey(WxConfig.getConf(WxConfig.KEY_PARTNER_KEY));
            queryReq.setGateUrl("https://gw.tenpay.com/gateway/verifynotifyid.xml");
            queryReq.setParameter(WxFields.F_partner,
                    WxConfig.getConf(WxConfig.KEY_PARTNER));
            queryReq.setParameter("notify_id", notify_id);

            // 通信对象
            httpClient.setTimeOut(5);
            // 设置请求内容
            try {
                httpClient.setReqContent(queryReq.getRequestURL());
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage());
                rsp.put(WxFields.F_result, WxFields.V_Fail);
                return false;
            }
            // 后台调用
            if (httpClient.call()) {
                // 设置结果参数
                try {
                    queryRes.setContent(httpClient.getResContent());
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                    rsp.put(WxFields.F_result, WxFields.V_Fail);
                    return false;
                }
                queryRes.setKey(WxConfig.getConf(WxConfig.KEY_PARTNER_KEY));
                rsp.putAll(queryRes.getAllParameters());
                // 获取返回参数
                String retcode = queryRes.getParameter("retcode");
                String trade_state = queryRes.getParameter("trade_state");
                String trade_mode = queryRes.getParameter("trade_mode");
                // 判断签名及结果
                if (queryRes.isTenpaySign() && "0".equals(retcode)
                        && "0".equals(trade_state) && "1".equals(trade_mode)) {
                    rsp.put(WxFields.F_result, WxFields.V_Success);
                    rsp.put(WxFields.F_transactionId,
                            queryRes.getParameter("transaction_id"));
                    return true;
                } else {
                    // 错误时，返回结果未签名，记录retcode、retmsg看失败详情。
                    // System.out.println("查询验证签名失败或业务错误");
                    // System.out.println("retcode:"
                    // + queryRes.getParameter("retcode") + " retmsg:"
                    // + queryRes.getParameter("retmsg"));

                }

            } else {
                // System.out.println("后台调用通信失败");
                // System.out.println(httpClient.getResponseCode());
                // System.out.println(httpClient.getErrInfo());
            }
        } else {
            // System.out.println("通知签名验证失败");
        }
        return false;
    }

    /**
     * 请求地址： https://api.weixin.qq.com/pay/orderquery?access_token=xxxxxx
     * 
     * 请求数据: { "appid" : "wwwwb4f85f3a797777", "package" :
     * "out_trade_no=11122&partner=1900090055&sign=4e8d0df3da0c3d0df38f",
     * "timestamp" : "1369745073", "app_signature" :
     * "53cca9d47b883bd4a5c85a9300df3da0cb48565c", "sign_method" : "sha1" }
     * 
     * 响应格式: { "errcode": 0, "errmsg": "ok", "order_info": { "ret_code": 0,
     * "ret_msg": "", "input_charset": "GBK", "trade_state": "0", "trade_mode":
     * "1", "partner": "1234567890", "bank_type": "CMB_FP", "bank_billno":
     * "201405273540085997", "total_fee": "1", "fee_type": "1",
     * "transaction_id": "1218614901201405273313473135", "out_trade_no":
     * "JfuKdiBig4zZnE4n", "is_split": "false", "is_refund": "false", "attach":
     * "", "time_end": "20140527194139", "transport_fee": "0", "product_fee":
     * "1", "discount": "0", "rmb_total_fee": "" } }
     * 
     * 参考: http://www.cnblogs.com/txw1958/p/wxpay-order-query.html
     * 
     * @param req
     * @param resp
     * @return
     * @throws Exception
     */
    public static String queryPrePay(Map<String, String> req) throws Exception {
        String orderNo = req.get(WxFields.F_orderNo);
        String timestamp = req.get(WxFields.F_timestamp);
        String token = req.get(WxFields.F_token);
        // packageValue
        PackageRequestHandler packageReqHandler = new PackageRequestHandler();
        packageReqHandler.setKey(WxConfig.getConf(WxConfig.KEY_PARTNER_KEY));
        packageReqHandler.setParameter(WxFields.F_out_trade_no, orderNo);
        packageReqHandler.setParameter(WxFields.F_partner,
                WxConfig.getConf(WxConfig.KEY_PARTNER));
        String packageValue = packageReqHandler.getRequestURL();

        // app_signature
        PrepayIdRequestHandler reqHandler = new PrepayIdRequestHandler();
        reqHandler.setParameter(WxFields.F_appid,
                WxConfig.getConf(WxConfig.KEY_APP_ID));
        reqHandler.setParameter(WxFields.F_appkey,
                WxConfig.getConf(WxConfig.KEY_APP_KEY));
        reqHandler.setParameter(WxFields.F_package, packageValue);
        // yyyyMMddHHmmss
        reqHandler.setParameter(WxFields.F_timestamp, timestamp);
        String appsign = reqHandler.createSHA1Sign();

        // postdata
        Map<String, String> postData = new HashMap<String, String>();
        postData.put(WxFields.F_appid, WxConfig.getConf(WxConfig.KEY_APP_ID));
        postData.put(WxFields.F_package, packageValue);
        postData.put(WxFields.F_timestamp, timestamp);
        postData.put(WxFields.F_app_signature, appsign);
        postData.put(WxFields.F_sign_method, WxFields.V_SHA1);

        // 通信对象
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setTimeOut(5);
        httpClient.callHttpPost(
                "https://api.weixin.qq.com/pay/orderquery?access_token="
                        + token, JSON.toJSONString(postData));
        return httpClient.getResContent();
    }

    /**
     * TODO 根据收到的请求数据，解析成map
     * 
     * @param content
     * @param req
     * @throws Exception
     */
    public static void verifyResponse(String content, Map<String, String> req) {
        try {
            req = WxCore.parseQString(content);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Parse wx response error {}", content);
        }
    }

    /**
     * 退款
     * 
     * @param pay
     * @param respMap
     * @return
     * @throws Exception
     */
    public static boolean refund(OrderWx pay, Map<String, String> rspMap)
            throws Exception {
        String out_refund_no = WxUtil.genTradeNo();
        rspMap.put("out_refund_no", out_refund_no);

        // 商户号
        String partner = WxConfig.getConf(WxConfig.KEY_PARTNER);

        // 密钥
        String key = WxConfig.getConf(WxConfig.KEY_PARTNER_KEY);

        // 创建查询请求对象
        RequestHandler reqHandler = new RequestHandler();
        // 通信对象
        TenpayHttpClient httpClient = new TenpayHttpClient();
        // 应答对象
        ClientResponseHandler resHandler = new ClientResponseHandler();

        // -----------------------------
        // 设置请求参数
        // -----------------------------
        reqHandler.init();
        reqHandler.setKey(key);
        reqHandler
                .setGateUrl("https://mch.tenpay.com/refundapi/gateway/refund.xml");

        // -----------------------------
        // 设置接口参数
        // -----------------------------
        reqHandler.setParameter("service_version", "1.1");
        reqHandler.setParameter("partner", partner);
        reqHandler.setParameter("out_trade_no", pay.orderNo);
        reqHandler.setParameter("transaction_id", pay.transactionId);
        reqHandler.setParameter("out_refund_no", out_refund_no);
        reqHandler.setParameter("total_fee", String.valueOf(pay.payAmount));
        reqHandler.setParameter("refund_fee", String.valueOf(pay.payAmount));
        reqHandler.setParameter("op_user_id", partner);
        // 操作员密码,MD5处理
        reqHandler.setParameter("op_user_passwd", key);

        reqHandler.setParameter("recv_user_id", pay.recvUserId);
        reqHandler.setParameter("recv_user_name", pay.recvUserName);
        // -----------------------------
        // 设置通信参数
        // -----------------------------
        // 设置请求返回的等待时间
        httpClient.setTimeOut(5);
        // 设置ca证书
        // httpClient.setCaInfo(new File("e:/cacert.pem"));

        // 设置个人(商户)证书
        // httpClient.setCertInfo(new File("e:/1900000109.pfx"), "1900000109");

        // 设置发送类型POST
        httpClient.setMethod("POST");

        // 设置请求内容
        String requestUrl = reqHandler.getRequestURL();
        httpClient.setReqContent(requestUrl);
        String rescontent = "null";

        // 后台调用
        if (httpClient.call()) {
            // 设置结果参数
            rescontent = httpClient.getResContent();
            resHandler.setContent(rescontent);
            resHandler.setKey(key);

            // 获取返回参数
            String retcode = resHandler.getParameter("retcode");

            // 判断签名及结果
            if (resHandler.isTenpaySign() && "0".equals(retcode)) {
                /*
                 * 退款状态 refund_status 4，10：退款成功。 3，5，6：退款失败。 8，9，11:退款处理中。 1，2:
                 * 未确定，需要商户原退款单号重新发起。
                 * 7：转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，资金回流到商户的现金帐号
                 * ，需要商户人工干预，通过线下或者财付通转账的方式进行退款。
                 */
                String refund_status = resHandler.getParameter("refund_status");
                String out_trade_no = resHandler.getParameter("out_trade_no");
                rspMap.put("refund_status", refund_status);
                rspMap.put(WxFields.F_orderNo, out_trade_no);
                return true;
            } else {
                // 错误时，返回结果未签名，记录retcode、retmsg看失败详情。
                rspMap.put(WxFields.F_rspCode,
                        resHandler.getParameter("retcode"));
                rspMap.put(WxFields.F_rspDesc,
                        resHandler.getParameter("retmsg"));
            }
        } else {
            LOG.error("后台调用通信失败 http rspCode->{} errInfo->{}",
                    httpClient.getResponseCode(), httpClient.getErrInfo());
            // 有可能因为网络原因，请求已经处理，但未收到应答。
        }

        return false;
        // 获取debug信息,建议把请求、应答内容、debug信息，通信返回码写入日志，方便定位问题
        // System.out.println("http res:" + httpClient.getResponseCode() + "," +
        // httpClient.getErrInfo());
        // System.out.println("req url:" + requestUrl);
        // System.out.println("req debug:" + reqHandler.getDebugInfo());
        // System.out.println("res content:" + rescontent);
        // System.out.println("res debug:" + resHandler.getDebugInfo());
    }
}
