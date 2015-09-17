/**
 * 
 */
package jframe.pay.domain;

/**
 * @author dzh
 * @date Jul 13, 2015 4:27:44 PM
 * @since 1.0
 */
public enum DevType {

	IOS(1), ANDRIOD(2), UNKNOWN(0);

	public final int type;

	private DevType(int type) {
		this.type = type;
	}

	public String toString() {
		return String.valueOf(type);
	}

	public boolean equals(String type) {
		return (type != null && this.toString().equals(type.toString())) ? true
				: false;
	}

}
