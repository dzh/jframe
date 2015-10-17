/**
 * 
 */
package jframe.pay.wx.domain;

import jframe.pay.domain.Fields;

/**
 * @author dzh
 * @date Sep 25, 2014 10:29:22 AM
 * @since 1.0
 */
public interface WxFields extends Fields {

    /**
     * 
     */
    String F_bank_type = "bank_type";

    String F_bank_billno = "bank_billno";

    String F_body = "body";

    String F_notify_url = "notify_url";

    String F_partner = "partner";

    String F_partner_key = "key";

    String F_out_trade_no = "out_trade_no";

    String F_trade_mode = "trade_mode";

    String F_total_fee = "total_fee";

    String F_spbill_create_ip = "spbill_create_ip";

    String F_fee_type = "fee_type";

    String F_input_charset = "input_charset";

    String F_appid = "appid";

    String F_mch_id = "mch_id";

    String F_appkey = "appkey";

    String F_noncestr = "noncestr";

    String F_nonce_str = "nonce_str";

    String F_trade_type = "trade_type";

    String F_package = "package";

    String F_timestamp = "timestamp";

    String F_packageValue = "packagevalue";

    String F_traceid = "traceid";

    String F_app_signature = "app_signature";

    String F_sign_method = "sign_method";

    String F_partnerid = "partnerid";

    String F_prepayid = "prepayid";

    String F_sign = "sign";

    String F_transactionId = "transactionId";

    String F_result = "result";

    String F_notify_id = "notify_id";

    String F_transport_fee = "transport_fee";

    String F_product_fee = "product_fee";

    String F_discount = "discount";

    String F_buyerAlias = "buyerAlias";

    /**
     * 异步响应成功
     */
    String V_Success = "Success";

    String F_return_code = "return_code";
    String F_return_msg = "return_msg";
    String F_prepay_id = "prepay_id";
    /**
     * 异步响应失败
     */
    String V_Fail = "fail";

    String V_SHA1 = "sha1";
}
