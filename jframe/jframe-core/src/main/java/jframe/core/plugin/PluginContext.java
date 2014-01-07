/**
 * 
 */
package jframe.core.plugin;

import java.util.Collection;

import jframe.core.conf.Config;

/**
 * <p>
 * 插件间共享对象
 * <li></li>
 * </p>
 * 
 * @ThreadSafe
 * @author dzh
 * @date Sep 12, 2013 2:48:10 PM
 * @since 1.0
 */
public interface PluginContext {

	void initContext(Config config);

	PluginRef getPlugin(int id);

	PluginRef getPlugin(String name);

	Collection<PluginRef> getPlugins();

	PluginRef regPlugin(Plugin plugin);

	PluginRef unregPlugin(Plugin plugin);

	Config getConfig();

	void dispose();

	void regPluginListener(PluginListener l);

	void unregPluginListener(PluginListener l);

	void notifyPluginEvent(PluginEvent event);
}
