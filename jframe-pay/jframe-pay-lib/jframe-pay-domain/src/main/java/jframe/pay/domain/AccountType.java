/**
 * 
 */
package jframe.pay.domain;

/**
 * @author dzh
 * @date Aug 30, 2014 7:22:59 AM
 * @since 1.0
 */
public enum AccountType {

	COMMON(1, "普通用户"), INSURANCE(2, "保险公司");

	public final int code;
	public final String desc;

	private AccountType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String toStringValue() {
		return String.valueOf(code);
	}

}
