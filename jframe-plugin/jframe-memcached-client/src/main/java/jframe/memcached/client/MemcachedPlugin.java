/**
 * 
 */
package jframe.memcached.client;

import jframe.core.plugin.DefPlugin;
import jframe.core.plugin.PluginContext;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.annotation.Plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Aug 16, 2014 4:40:38 PM
 * @since 1.0
 */
@Plugin(startOrder = 1)
public class MemcachedPlugin extends DefPlugin {
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

}
