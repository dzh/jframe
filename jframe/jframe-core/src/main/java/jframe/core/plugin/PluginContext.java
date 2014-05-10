/**
 * 
 */
package jframe.core.plugin;

import java.util.Collection;
import java.util.Comparator;

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

	void regPlugins(Collection<Plugin> plugins,
			Comparator<? super Plugin> comparator);

	void unregPlugins(Collection<PluginRef> refs,
			Comparator<? super Plugin> comparator);

	/**
	 * use unregPlugin(PluginRef)
	 * 
	 * @param plugin
	 * @return
	 */
	@Deprecated
	PluginRef unregPlugin(Plugin plugin);

	void unregPlugin(PluginRef ref);

	Config getConfig();

	void dispose();

	void regPluginListener(PluginListener l);

	void unregPluginListener(PluginListener l);

	void notifyPluginEvent(PluginEvent event);
}
