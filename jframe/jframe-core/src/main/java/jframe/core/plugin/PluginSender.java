/**
 * 
 */
package jframe.core.plugin;

import java.util.LinkedList;
import java.util.List;

import jframe.core.dispatch.Dispatcher;
import jframe.core.msg.Msg;
import jframe.core.msg.PluginMsg;
import jframe.core.plugin.annotation.DispatchAdd;
import jframe.core.plugin.annotation.DispatchRemove;
import jframe.core.plugin.annotation.Message;
import jframe.core.plugin.annotation.MsgSend;

/**
 * @author dzh
 * @date Sep 30, 2013 3:13:40 PM
 * @since 1.0
 */
@Message(isSender = true)
public class PluginSender extends DefPlugin {

	private List<Dispatcher> _dispatcher;

	public PluginSender() {
		super();
	}

	public void init(PluginContext context) throws PluginException {
		super.init(context);
		_dispatcher = new LinkedList<Dispatcher>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.DispatchSource#send(jframe.core.msg.Msg)
	 */
	@MsgSend
	public void send(Msg<?> msg) {
		msg.setMeta(PluginMsg.PluginName, getName());
		for (Dispatcher d : _dispatcher) {
			d.receive(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.dispatch.DispatchSource#removeDispatch(jframe.core.dispatch
	 * .Dispatcher)
	 */
	@DispatchRemove
	public void removeDispatch(Dispatcher d) {
		_dispatcher.remove(d);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.dispatch.DispatchSource#addDispatch(jframe.core.dispatch.
	 * Dispatcher)
	 */
	@DispatchAdd
	public void addDispatch(Dispatcher d) {
		_dispatcher.add(d);
	}

}
