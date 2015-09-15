/**
 * 
 */
package jframe.pay.domain.http;

import java.util.Map;

import jframe.pay.domain.Fields;

/**
 * 0成功，100之后订单相关，200以后用户相关
 * 
 * @author dzh
 * @date Jul 12, 2014 7:39:11 AM
 * @since 1.0
 */
public enum RspCode {

	SUCCESS("00", "成功"), FAIL_UNKNOWN("-1", "未知错误"),
	/***************************** 订单相关错误 **********************************/
	/* FAIL_ORDER("100", "订单创建失败，检查请求参数"), */FAIL_ORDER_REPEAT("101", "订单号重复"), FAIL_ORDER_UNDO(
			"102", "订单未付款"), FAIL_PAYNO_NOEXIST("103", "支付号不存在"), FAIL_ORDER_OPER(
			"104", "订单操作不符合条件"), FAIL_GROUP_CANCEL_ERROR("105", "订单组取消失败"), FAIL_PAYNO_EXIST(
			"106", "支付号已存在"), FAIL_PAYTYPE_NOEXIST("107", "支付类型不存在"),
	/***************************** 用户相关错误 **********************************/
	FAIL_USER_EXIST("200", "用户ID已存在"), FAIL_MOBILE_EXIST("201", "手机号已存在"), FAIL_USER_NOT_FOUND(
			"202", "用户不存在"), FAIL_CARD_EXIST("203", "银行卡不存在"),
	/***************************** 网络或服务器相关错误 **********************************/
	FAIL_NET("300", "网络异常"), FAIL_SERVER("301", "服务器内部错误"), FAIL_HTTP_TOKAN(
			"303", "无效token"), FAIL_HTTP_PATH("304", "无效路径"), FAIL_HTTP_REQOP(
			"305", "无效操作码"), FAIL_HTTP_MISS_PARA("306", "缺少必要参数"),
	/***************************** 提现相关错误 **********************************/
	FAIL_PARAM("400", "参数不正确"), FAIL_VCODE("401", "验证码不正确"), FAIL_DR_DO("402",
			"提现请求已处理"), FAIL_ACCOUNT_NOT_FOUND("403", "账户不存在"), FAIL_ACCOUNT(
			"404", "金额不足"), FAIL_ACCOUNT_INCOME("405", "账户详细参数不正确"),
	/***************************** 数据库相关错误 **********************************/
	FAIL_DB_Conn("500", "数据库连接出错"),
	/***************************** 客户端错误 **********************************/
	FAIL_CLIENT_RECV("700", "返回数据错误"), FAIL_CLIENT_REQOP("701", "请求参数或方法错误"), FAIL_CLIENT_SSL(
			"702", "启用https失败"),
	/***************************** 支付错误 **********************************/
	FAIL_PAY_AMOUNT("800", "交易金额或账户收入不正确"),

	/***************************** UPMP相关错误 **********************************/

	FAIL_REQ("600", "请求报文错误"), FAIL_SIGN("601", "签名验证失败"), FAIL_TRAD("602",
			"交易失败"), FAIL_SESSION_TIMEOUT("603", "会话超时"), FAIL_ORDER_STATUS(
			"604", "订单状态错误"), FAIL_PAY_REPEAT("605", "重复支付"), FAIL_CARD_ERROR(
			"606", "银行卡信息错误"), FAIL_ORIGINAL_AMOUNT_ERROR("607", "原始金额错误"), FAIL_AMOUNT_OVER(
			"608", "交易金额超限"), FAIL_MS_VCODE("609", "短信验证码错误"), FAIL_ORDER_NOT_FOUND(
			"610", "没有找到订单"),
	/****************************** 安全相关 *************************************/
	FAIL_SIGN_NOT_FOUND("900", "非法请求"), FAIL_SIGN_ERROR("901", "请求验证失败"), FAIL_ALIPAY_BACK_SIGN_ERROR(
			"902", "支付宝返回验签失败"), FAIL_ALIPAY_BACK_UNKOWN_STATUS_ERROR("903",
			"支付宝返回状态未知"),

	/******************************* 微信相关[2000,3000) **************************************/
	FAIL_TOKEN("2000", "非法Token"), FAIL_PREPAY("2001", "预支付执行失败"),

	;

	public String code;
	public String desc;

	private RspCode(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public boolean equals(RspCode code) {
		if (code == null)
			return false;
		return code.code.equals(code) ? true : false;
	}

	public static void setRspCode(Map<String, Object> rsp, RspCode rc) {
		rsp.put(Fields.F_rspCode, rc.code);
		rsp.put(Fields.F_rspDesc, rc.desc);
	}

}
