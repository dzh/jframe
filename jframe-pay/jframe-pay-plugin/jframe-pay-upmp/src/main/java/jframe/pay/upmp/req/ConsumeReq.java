/**
 * 
 */
package jframe.pay.upmp.req;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKUtil;

import jframe.pay.domain.PayCurrency;
import jframe.pay.domain.util.IDUtil;
import jframe.pay.upmp.domain.UpmpConfig;

/**
 * @author dzh
 * @date Nov 25, 2015 4:09:58 PM
 * @since 1.0
 */
public class ConsumeReq extends UpmpSdk {

    public static boolean consume(Map<String, String> req, Map<String, Object> rsp) {
        /**
         * 组装请求报文
         */
        Map<String, String> data = new HashMap<String, String>();
        // 版本号
        data.put("version", UpmpConfig.getConf(UpmpConfig.KEY_VERSION));
        // 字符集编码 默认"UTF-8"
        data.put("encoding", UpmpConfig.getConf(UpmpConfig.KEY_CHARSET));
        // 签名方法 01 RSA
        data.put("signMethod", "01");
        // 交易类型 01-消费
        data.put("txnType", "01");
        // 交易子类型 01:自助消费 02:订购 03:分期付款
        data.put("txnSubType", "01");
        // 业务类型
        data.put("bizType", "000201");
        // 渠道类型，07-PC，08-手机
        data.put("channelType", "08");
        // 前台通知地址 ，控件接入方式无作用
        data.put("frontUrl", UpmpConfig.getConf(UpmpConfig.KEY_FRONT_URL));
        // 后台通知地址
        data.put("backUrl", UpmpConfig.getConf(UpmpConfig.KEY_BACK_URL));
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        data.put("accessType", "0");
        // 商户号码，请改成自己的商户号
        data.put("merId", UpmpConfig.getConf(UpmpConfig.KEY_MER_ID));
        // 商户订单号，8-40位数字字母
        String orderNo = IDUtil.genOrderNo();
        req.put(F_orderNo, orderNo);
        data.put("orderId", orderNo);
        // 订单发送时间，取系统时间
        data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        // 交易金额，单位分
        data.put("txnAmt", req.get(F_payAmount));
        // 交易币种
        data.put("currencyCode", PayCurrency.CNY.code);
        // 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
        // data.put("reqReserved", "透传信息");
        // 订单描述，可不上送，上送时控件中会显示该信息
        // data.put("orderDesc", "订单描述");

        data = signData(data);

        // 交易请求url 从配置文件读取
        String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();

        Map<String, String> resmap = submitUrl(data, requestAppUrl);
        rsp.putAll(resmap);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Consume req->{} rsp->{}", data.toString(), resmap.toString());
        }

        return UpmpConfig.RESPONSE_CODE_SUCCESS.equals(resmap.get("respCode")) ? true : false;
    }

    public static boolean backPay(Map<String, String> req, Map<String, Object> rsp)
            throws UnsupportedEncodingException {
        return UpmpConfig.RESPONSE_CODE_SUCCESS.equals(req.get("respCode")) && validateData(req);
    }

    private static boolean validateData(Map<String, String> req) throws UnsupportedEncodingException {
        Map<String, String> valideData = null;
        boolean r = false;
        if (null != req && !req.isEmpty()) {
            Iterator<Entry<String, String>> it = req.entrySet().iterator();
            valideData = new HashMap<String, String>(req.size());
            while (it.hasNext()) {
                Entry<String, String> e = it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                value = new String(value.getBytes("ISO-8859-1"), encoding);
                valideData.put(key, value);
            }
            r = SDKUtil.validate(valideData, valideData.get(F_param_encoding));
        }

        if (LOG.isDebugEnabled() && !r) {
            LOG.error("validateData false valideData -> {}", valideData);
        }
        r = true; // TODO
        return r;
    }

}
