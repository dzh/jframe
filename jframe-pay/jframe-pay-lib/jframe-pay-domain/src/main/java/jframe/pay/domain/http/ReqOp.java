package jframe.pay.domain.http;

/**
 * 
 * @author dzh
 * @date Jul 13, 2015 2:52:27 PM
 * @since 1.0
 */
public enum ReqOp {
	/************************** Account *********************************/
	REG("reg", "register account"), UPU("upu", "update account"), QRYU("qryu",
			"query account"),

	/************************** Order **********************************/
	CONSUME("consume", "consume"), QRYOD("qryod", "query order"), ALIBACK(
			"aliback", "alipay back"), WXBACK("wxback", "wxpay back"), ALICLIENTBACK(
			"aliclientback", "alipay client back"),UPMPBACK("upmpback","upmppay back")

	;

	public final String code;
	public final String desc;

	private ReqOp(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

}
