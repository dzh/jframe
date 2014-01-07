/**
 * 
 */
package jframe.core.dispatch;

import jframe.core.msg.Msg;

/**
 * @author dzh
 * @date Jun 18, 2013 4:22:57 PM
 */
public interface DispatchTarget {

	void receive(Msg<?> msg);

	boolean interestMsg(Msg<?> msg);

}
