/**
 * 
 */
package com.alipay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import jframe.pay.alipay.AlipayConfig;
import jframe.pay.domain.Fields;

/**
 * @date Nov 28, 2014 10:16:07 AM
 * @since 1.0
 */
public class OrderUtil {

    public static String buildOrderInfo(Map<String, String> req) {
        StringBuilder buf = new StringBuilder();
        // 合作者身份ID
        buf.append("partner=\"" + AlipayConfig.getConf(AlipayConfig.PARTNER) + "\"");
        // 卖家支付宝账号
        buf.append("&seller_id=\"" + AlipayConfig.getConf(AlipayConfig.SELLER_ID) + "\"");
        // 商户网站唯一订单号
        buf.append("&out_trade_no=\"" + req.get(Fields.F_orderNo) + "\"");
        // 商品名称
        buf.append("&subject=\"" + AlipayConfig.getConf(AlipayConfig.SUBJECT) + "\"");
        // 商品详情
        buf.append("&body=\"" + req.get(Fields.F_payDesc) + "\"");
        // 商品金额
        buf.append("&total_fee=\"" + (Long.parseLong(req.get(Fields.F_payAmount)) / 100.0) + "\"");
        // 服务器异步通知页面路径
        buf.append("&notify_url=\"" + AlipayConfig.getConf(AlipayConfig.NOTIFY_URL) + "\"");
        // 接口名称， 固定值
        buf.append("&service=\"" + AlipayConfig.getConf(AlipayConfig.SERVICE) + "\"");
        // 支付类型， 固定值
        buf.append("&payment_type=\"" + AlipayConfig.getConf(AlipayConfig.PAYMENT_TYPE) + "\"");
        // 参数编码， 固定值
        buf.append("&_input_charset=\"" + AlipayConfig.getConf(AlipayConfig.INPUT_CHARSET) + "\"");

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        buf.append("&it_b_pay=\"" + AlipayConfig.getConf(AlipayConfig.IT_B_PAY) + "\"");

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        // orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值
        // orderInfo += "&paymethod=\"expressGateway\"";
        return buf.toString();
    }

    public static String genPayInfo(String od) {
        String sign = AlipayConfig.sign(od);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return od + "&sign=\"" + sign + "\"&" + AlipayConfig.getSignType();
    }

}
