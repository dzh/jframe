/**
 * 
 */
package jframe.activemq;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginRecver;

/**
 * <>
 * @author dzh
 * @date Jul 31, 2015 1:07:25 PM
 * @since 1.0
 */
public class ActivemqPlugin extends PluginRecver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRecver#doRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected void doRecvMsg(Msg<?> msg) {
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRecver#canRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected boolean canRecvMsg(Msg<?> msg) {
		
		return false;
	}
	
	
}
