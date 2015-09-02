/**
 * 
 */
package jframe.pay.http.usr;

import java.util.Map;

import jframe.pay.domain.http.ReqOp;
import jframe.pay.domain.http.RspCode;
import jframe.pay.http.handler.PayException;
import jframe.pay.http.handler.PayHandler;

/**
 * @author dzh
 * @date Sep 1, 2015 7:30:44 PM
 * @since 1.0
 */
public class UsrHandler extends PayHandler {

	static final UsrService svc = new UsrService();

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
		if (ReqOp.REG.code.equals(reqOp)) {
			svc.reg(req, rsp);
		} else if (ReqOp.UPU.code.equals(reqOp)) {
			svc.upu(req, rsp);
		} else if (ReqOp.QRYU.code.equals(reqOp)) {
			svc.qryu(req, rsp);
		} else {
			RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_REQOP);
			throw new PayException("Not found");
		}
	}

}
