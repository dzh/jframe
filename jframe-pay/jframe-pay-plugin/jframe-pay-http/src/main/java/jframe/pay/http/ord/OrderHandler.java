/**
 * 
 */
package jframe.pay.http.ord;

import java.util.Map;

import jframe.pay.domain.http.ReqOp;
import jframe.pay.domain.http.RspCode;
import jframe.pay.http.handler.PayException;
import jframe.pay.http.handler.PayHandler;

/**
 * @author dzh
 * @date Sep 1, 2015 7:31:17 PM
 * @since 1.0
 */
public class OrderHandler extends PayHandler {

	static final OrderService svc = new OrderService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.pay.http.handler.PayHandler#doService(java.util.Map,
	 * java.util.Map)
	 */
	@Override
	protected void doService(Map<String, String> req, Map<String, Object> rsp)
			throws Exception {
		String reqOp = getReqOp();
		if (ReqOp.CONSUME.code.equals(reqOp)) {
			svc.consume(req, rsp);
		} else if (ReqOp.QRYOD.code.equals(reqOp)) {
			svc.qryod(req, rsp);
		} else if (ReqOp.ALIBACK.code.equals(reqOp)) {
			svc.aliback(req, rsp);
		} else {
			RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_REQOP);
			throw new PayException("Not found");
		}
	}

}
