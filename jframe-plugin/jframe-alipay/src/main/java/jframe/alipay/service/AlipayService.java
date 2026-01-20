package jframe.alipay.service;

import com.alipay.api.AlipayClient;
import jframe.core.plugin.annotation.Service;

import java.util.Map;

/**
 * https://docs.open.alipay.com/204 App支付
 * https://docs.open.alipay.com/291/105971  密钥配置
 * https://opendocs.alipay.com/open/203/105286 支付Notify说明
 * <p>
 * https://docs.open.alipay.com/270/105899/
 * https://docs.open.alipay.com/api_1/alipay.trade.page.pay/
 * <p>
 * https://openhome.alipay.com/platform/demoManage.htm#/alipay.trade.page.pay
 * https://openhome.alipay.com/platform/appDaily.htm?tab=info
 * https://sandbox.alipaydev.com/user/accountDetails.htm?currentBar=1
 *
 * @author dzh
 * @date 2019-07-22 14:56
 */
@Service(clazz = "jframe.alipay.service.AlipayServiceImpl", id = AlipayService.ID)
public interface AlipayService {

    String ID = "jframe.service.alipay";

    String F_URL = "url";
    String F_APP_ID = "app.id";
    String F_PRIVATE_KEY = "private.key";
    String F_PUBLIC_KEY = "public.key";
    String F_FORMAT = "format";
    String F_CHARSET = "charset";
    String F_SIGN_TYPE = "sign.type";
    String F_ENCRYPT_KEY = "encrypt.key";
    String F_ENCRYPT_TYPE = "encrypt.type";
    String F_RETURN_URL = "return.url";
    String F_NOTIFY_URL = "notify.url";

    String TRADE_STATUS_WAIT = "WAIT_BUYER_PAY";    //交易创建，等待买家付款
    String TRADE_STATUS_CLOSED = "TRADE_CLOSED";    //未付款交易超时关闭，或支付完成后全额退款
    String TRADE_STATUS_SUCC = "TRADE_SUCCESS";     //交易支付成功
    String TRADE_STATUS_FINISHED = "TRADE_FINISHED";    //交易结束，不可退款

    //https://opendocs.alipay.com/support/01raw4
    String TRADE_NOTIFY_RES_SUCC = "success";//接收成功
    String TRADE_NOTIFY_RES_FAIL = "fail"; //接收失败,重新发送通知

    AlipayClient getClient(String id);

    /**
     * @param id
     * @param param 异步返回参数
     * @return
     */
    boolean checkAsyncReturn(String id, Map<String, String> param);

    String getConf(String id, String key);
}
