/**
 * 
 */
package jframe.pay.http.handler;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

import jframe.pay.domain.http.RspCode;

/**
 * @author dzh
 * @date Aug 15, 2014 7:42:11 PM
 * @since 1.0
 */
public abstract class SafeHandler extends AbstractHandler {

	/**
	 * @param plugin
	 */
	protected SafeHandler() {
		super();
	}

	protected Map<String, String> parseHttpReq(String content) {
		return super.parseHttpReq(content);
	}

	@Override
	protected boolean isValidData(String data) {

		return true;
	}

	@Override
	public boolean isValidHeaders(HttpHeaders headers) {
		if (!isValidToken(headers)) {
			RspCode.setRspCode(rspMap, RspCode.FAIL_HTTP_TOKAN);
			return false;
		}

		return true;
	}

	@Override
	protected Map<String, Object> filterRspMap(Map<String, Object> rsp) {
		return rsp;
	}

	@Override
	public void service(Map<String, String> req, Map<String, Object> rsp)
			throws PayException {
		calcSignContent(req);
	}

	/**
	 * TODO throw exception
	 * 
	 * @param req
	 */
	private void calcSignContent(Map<String, String> req) {
		// HttpHeaders headers = this.getHttpHeaders();
		// String signK = headers.get(PayFields.F_Pay_SignK);
		// String signV = headers.get(PayFields.F_Pay_SignV);
		// if (req == null || signK == null || signV == null) {
		// isValidContent = false;
		// return;
		// }
		// isValidContent = SecurityUtil.payDigest(signK, JsonUtil.encode(req))
		// .equals(signV) ? true : false;
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	public boolean isValidContent() {
		return true;
	}

	public boolean isValidToken(HttpHeaders headers) {
		if (Boolean.parseBoolean(Plugin.getConfig("token.disable", "false"))) {
			return true;
		}

		return false;
	}
}
