/**
 * 
 */
package jframe.pay.http.ord;

import java.util.Map;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.pay.alipay.service.AlipayService;
import jframe.pay.domain.PayType;
import jframe.pay.domain.http.RspCode;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.http.usr.service.CommonService;

/**
 * @author dzh
 * @date Sep 1, 2015 8:29:42 PM
 * @since 1.0
 */
@Injector
public class OrderService extends CommonService {

	@InjectService(id = "jframe.pay.service.alipay")
	static AlipayService Alipay;

	public void consume(Map<String, String> req, Map<String, Object> rsp)
			throws Exception {
		// check req
		if (HttpUtil.mustReq(req, F_payType, F_payGroup, F_payNo, F_transType,
				F_payAmount, F_account).size() > 0) {
			RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
			return;
		}

		int payType = Integer.parseInt(req.get(F_payType));
		if (PayType.A.type == payType) {
			Alipay.pay(req, rsp);
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

	}

}
