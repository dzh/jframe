/**
 * 
 */
package jframe.pay.http.usr;

import java.util.Map;

import jframe.pay.domain.http.RspCode;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.http.usr.service.CommonService;

/**
 * @author dzh
 * @date Sep 1, 2015 8:29:17 PM
 * @since 1.0
 */
public class UsrService extends CommonService {

	public void reg(Map<String, String> req, Map<String, Object> rsp) {
		// check req
		if (HttpUtil.mustReq(req, F_mobile, F_type).size() > 0) {
			RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
			return;
		}

		// insert

		// response
	}

	public void upu(Map<String, String> req, Map<String, Object> rsp) {

	}

	public void qryu(Map<String, String> req, Map<String, Object> rsp) {

	}

}
