/**
 * 
 */
package jframe.email;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginRecver;

/**
 * @author dzh
 * @date Jun 7, 2014 2:21:13 PM
 * @since 1.0
 */
public class EmailClientPlugin extends PluginRecver {

	@Override
	public void start() throws PluginException {
		super.start();
	}

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
