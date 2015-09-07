/**
 * 
 */
package jframe.pay.task;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginRecver;

/**
 * @author dzh
 * @date Sep 6, 2015 3:21:07 PM
 * @since 1.0
 */
public class PayTaskPlugin extends PluginRecver {

	/* (non-Javadoc)
	 * @see jframe.core.plugin.PluginRecver#doRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected void doRecvMsg(Msg<?> msg) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jframe.core.plugin.PluginRecver#canRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected boolean canRecvMsg(Msg<?> msg) {
		// TODO Auto-generated method stub
		return false;
	}

}
