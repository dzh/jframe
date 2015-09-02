/**
 * 
 */
package jframe.pay.domain;

/**
 * @author dzh
 * @date Jul 21, 2014 6:29:45 PM
 * @since 1.0
 */
public enum TransStatus {

	SUCCESS("00", "交易成功结束"), PROCESSING("01", "处理中"), FAILURE("03", "交易处理失败");
	public final String code;
	public final String desc;

	TransStatus(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

}
