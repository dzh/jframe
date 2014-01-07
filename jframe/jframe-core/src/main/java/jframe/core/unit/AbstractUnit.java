/**
 * 
 */
package jframe.core.unit;

import jframe.core.Frame;
import jframe.core.signal.Signal;

/**
 * @author dzh
 * @date Sep 23, 2013 8:40:27 PM
 * @since 1.0
 */
public abstract class AbstractUnit implements Unit {

	private int _id;
	protected Frame _frame;
	private String _name;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.unit.Unit#getID()
	 */
	public int getID() {
		return _id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.unit.Unit#sendSig(jframe.core.signal.Signal)
	 */
	public void sendSig(Signal sig) {
		_frame.broadcast(sig);
	}

	public void init(Frame frame) throws UnitException {
		if (frame == null)
			throw new UnitException("frame is null when initialize unit "
					+ getID() + "-" + getName());
		this._frame = frame;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Unit && ((Unit) obj).getID() == this.getID())
			return true;
		return false;
	}

	public void setID(int id) {
		this._id = id;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getName() {
		return _name;
	}

	public Frame getFrame() {
		return _frame;
	}

	public String getConfig(String key) {
		return _frame.getConfig().getConfig(key);
	}

}
