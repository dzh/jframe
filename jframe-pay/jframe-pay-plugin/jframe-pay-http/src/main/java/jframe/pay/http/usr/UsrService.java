/**
 * 
 */
package jframe.pay.http.usr;

import java.util.Map;

import jframe.pay.domain.AccountType;
import jframe.pay.domain.SexCode;
import jframe.pay.domain.dao.UsrAccount;
import jframe.pay.domain.http.RspCode;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.domain.util.IDUtil;
import jframe.pay.domain.util.JsonUtil;
import jframe.pay.http.usr.service.CommonService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 1, 2015 8:29:17 PM
 * @since 1.0
 */
public class UsrService extends CommonService {

	protected static Logger LOG = LoggerFactory.getLogger(UsrService.class);

	public void reg(Map<String, String> req, Map<String, Object> rsp) {
		// check req
		if (HttpUtil.mustReq(req, F_mobile).size() > 0) {
			RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
			return;
		}

		UsrAccount usr = new UsrAccount();
		usr.account = IDUtil.genUsrAccount();
		usr.mobile = req.get(F_mobile);
		usr.name = req.getOrDefault(F_name, req.get(F_mobile));
		usr.type = Integer.parseInt(req.getOrDefault(F_type,
				AccountType.COMMON.toStringValue()));
		usr.passwd = req.get(F_passwd);
		usr.sex = Integer.parseInt(req.getOrDefault(F_sex,
				SexCode.UNKNOWN.toStringValue()));
		usr.email = req.get(F_email);
		// insert
		PayDao.insertUsrAccount(usr);

		// response
		rsp.put(F_account, usr.account);

		LOG.info("Reg new account {}->{}", usr.mobile, usr.account);
	}

	public void upu(Map<String, String> req, Map<String, Object> rsp) {
		// check req
		if (HttpUtil.mustReq(req, F_account).size() > 0) {
			RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
			return;
		}

		UsrAccount usr = new UsrAccount();
		usr.account = req.get(F_account);
		usr.name = req.get(F_name);
		usr.mobile = req.get(F_mobile);
		// usr.passwd = req.get(F_passwd);
		if (req.containsKey(F_sex)) {
			usr.sex = Integer.parseInt(req.get(F_sex));
		}
		usr.email = req.get(F_email);

		// update
		PayDao.updateUsrAccount(usr);
	}

	public void qryu(Map<String, String> req, Map<String, Object> rsp) {
		// check req
		if (HttpUtil.mustReq(req, F_account).size() > 0) {
			RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
			return;
		}

		UsrAccount usr = PayDao.selectUsrAccount(req.get(F_account));
		if (usr == null) {
			RspCode.setRspCode(rsp, RspCode.FAIL_ACCOUNT_NOT_FOUND);
			return;
		}
		rsp.put(F_account, JsonUtil.toJson(usr));
	}

}
