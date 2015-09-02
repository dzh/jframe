/**
 * 
 */
package jframe.pay.domain;

/**
 * 交易类型
 * 
 * @author dzh
 * @date Jul 12, 2014 7:24:48 AM
 * @since 1.0
 */
public enum TransType {

	/**
	 * 消费
	 */
	Consume("01"),
	/**
	 * 消费撤销
	 */
	ConsumeCancel("31"),
	/**
	 * 预授权
	 */
	PreAuth("02"),
	/**
	 * 预授权撤销
	 */
	PreAuthCancel("32"),
	/**
	 * 预授权完成
	 */
	PreAuthFnh("03"),
	/**
	 * 预授权完成撤销
	 */
	PreAuthFnhCancel("33"),
	/**
	 * 退货
	 */
	ReturnGoods("04"),

	/**
	 * 余额查询
	 */
	BalanceInquiry("71"),
	/**
	 * 账户验证
	 */
	AccountVerification("72"),
	/**
	 * 账单缴费
	 */
	BillPayment("81"),
	/**
	 * 信用卡还款
	 */
	CreditcardPayments("82");

	public final String code;

	TransType(String code) {
		this.code = code;
	}

	public static final TransType[] TYPE_PUSH = new TransType[] { Consume,
			PreAuth };

	// public static final TransType[] TYPE_QUERY = new
	// TransType[]{Consume,PreAuth};
	/**
	 * TODO PreAuthFnh放在这里不合适?
	 */
	public static final TransType[] TYPE_CANCEL = new TransType[] {
			ConsumeCancel, PreAuthCancel, PreAuthFnh, PreAuthFnhCancel,
			ReturnGoods };

	public static final TransType[] TYPE_QUERY = new TransType[] {
			ConsumeCancel, PreAuthCancel, PreAuthFnh, PreAuthFnhCancel,
			ReturnGoods };

	public static boolean isConsume(String code) {
		if (TransType.Consume.code.equals(code)
				|| TransType.ConsumeCancel.code.equals(code))
			return true;

		return false;
	}

	public static boolean isPreAuth(String code) {
		if (TransType.PreAuth.code.equals(code)
				|| TransType.PreAuthCancel.code.equals(code)
				|| TransType.PreAuthFnh.code.equals(code)
				|| TransType.PreAuthFnhCancel.code.equals(code))
			return true;
		return false;
	}

}
