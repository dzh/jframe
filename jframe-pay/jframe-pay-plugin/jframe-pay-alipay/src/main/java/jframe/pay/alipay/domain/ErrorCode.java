/**
 * 
 */
package jframe.pay.alipay.domain;

/**
 * @author dzh
 * @date Nov 26, 2014 4:20:27 PM
 * @since 1.0
 */
public enum ErrorCode {

	PAY_SUCCESS(9000, "订单支付成功"), PAY_PROCESS(8000, "正在处理中"), PAY_FAILURE(4000,
			"订单支付失败"), USR_CANCEL(6001, "用户中途取消"), NET_ERROR(6002, "网络连接出错");

	public int code;
	public String desc;

	private ErrorCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}
}
