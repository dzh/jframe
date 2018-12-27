/**
 * 
 */
package jframe.ext.dispatch.activemq;

import jframe.core.msg.Msg;

/**
 * @author dzh
 * @date Oct 17, 2014 2:24:28 PM
 * @since 1.0
 */
public interface MsgTransfer {

	String encode(Msg<?> msg);

	Msg<?> decode(String msg);

}
