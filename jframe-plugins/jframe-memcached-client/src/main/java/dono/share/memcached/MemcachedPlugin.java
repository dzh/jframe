/**
 * 
 */
package dono.share.memcached;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginContext;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginSenderRecver;
import jframe.core.plugin.annotation.Plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Aug 16, 2014 4:40:38 PM
 * @since 1.0
 */
@Plugin(startOrder = 1)
public class MemcachedPlugin extends PluginSenderRecver {
	static final Logger LOG = LoggerFactory.getLogger(MemcachedPlugin.class);

	public void init(PluginContext context) throws PluginException {
		super.init(context);
	}

	public void start() throws PluginException {
		super.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.Plugin#stop()
	 */
	public void stop() throws PluginException {
		super.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.Plugin#destroy()
	 */
	public void destroy() throws PluginException {
		super.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginSenderRecver#doRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected void doRecvMsg(Msg<?> msg) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.plugin.PluginSenderRecver#canRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected boolean canRecvMsg(Msg<?> msg) {
		return false;
	}

}
