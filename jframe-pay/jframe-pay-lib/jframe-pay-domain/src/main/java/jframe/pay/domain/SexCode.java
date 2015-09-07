/**
 * 
 */
package jframe.pay.domain;

/**
 * @author dzh
 * @date Sep 6, 2015 2:41:16 PM
 * @since 1.0
 */
public enum SexCode {

	WOMAN(0), MAN(1), UNKNOWN(2);

	public final int code;

	private SexCode(int code) {
		this.code = code;
	}

	public String toStringValue() {
		return String.valueOf(code);
	}

}
