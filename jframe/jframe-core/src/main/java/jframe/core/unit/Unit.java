/**
 * 
 */
package jframe.core.unit;

import jframe.core.Frame;
import jframe.core.signal.Signal;

/**
 * <p>
 * Frame is consisted of many units, One unit has below features:
 * <li>Units communicate with signal</li>
 * <li>Frame broadcasts signal</li>
 * </p>
 * TODO canStop
 * 
 * @author dzh
 * @date Sep 18, 2013 11:19:01 AM
 * @since 1.0
 */
public interface Unit {

	void setID(int id);

	int getID();

	void setName(String name);

	String getName();

	void sendSig(Signal sig);

	void recvSig(Signal sig);

	void init(Frame frame) throws UnitException;

	void start() throws UnitException;

	void stop() throws UnitException;
}
