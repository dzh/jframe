/**
 * 
 */
package jframe.core.plugin;

import jframe.core.msg.Msg;
import jframe.core.msg.PluginMsg;
import jframe.core.plugin.annotation.Message;
import jframe.core.plugin.annotation.MsgInterest;
import jframe.core.plugin.annotation.MsgRecv;

/**
 * @author dzh
 * @date Oct 14, 2013 8:15:54 AM
 * @since 1.0
 */
@Message(isSender = true, isRecver = true)
public abstract class PluginSenderRecver extends PluginSender {

	public PluginSenderRecver() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.DispatchTarget#receive(jframe.core.msg.Msg)
	 */
	@MsgRecv
	public void receive(Msg<?> msg) {
		doRecvMsg(msg);
	}

	/**
	 * @param msg
	 */
	abstract protected void doRecvMsg(Msg<?> msg);

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.DispatchTarget#interestMsg(jframe.core.msg.Msg)
	 */
	@MsgInterest
	public boolean interestMsg(Msg<?> msg) {
		if (msg == null || PluginStatus.STOP == getStatus()
				|| getName().equals(msg.getMeta(PluginMsg.PluginName)))
			return false;
		return canRecvMsg(msg);
	}

	/**
	 * @param msg
	 * @return
	 */
	abstract protected boolean canRecvMsg(Msg<?> msg);

}
