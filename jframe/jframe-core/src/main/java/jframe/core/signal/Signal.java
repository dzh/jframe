/**
 * 
 */
package jframe.core.signal;

import java.util.Date;

import jframe.core.unit.Unit;

/**
 * @author dzh
 * @date Sep 23, 2013 2:28:22 PM
 * @since 1.0
 */
public class Signal {

	private Unit source;

	/**
	 * @param source
	 */
	public Signal() {
	}

	private int type;

	public Unit getSource() {
		return source;
	}

	public Signal setSource(Unit source) {
		this.source = source;
		return this;
	}

	public Signal setSigType(int type) {
		this.type = type;
		return this;
	}

	public int getSigType() {
		return this.type;
	}

	public static final int SIG_APP_EXIT = 0x0001;

	public static final int SIG_FRAME_STOP = 0x0011;
	public static final int SIG_FRAME_RESTART = 0x0012;

	public static final int SIG_UNIT_STOP = 0x0021;

	public static final Signal newSig(int type) {
		return new Signal().setSigType(type);
	}

	public String toString() {
		return "Signal Detail: Signal Type is " + type + "; Signal Source is "
				+ source.toString() + "; Date is " + new Date().toString();
	}

}
