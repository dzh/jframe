/**
 * 
 */
package jframe.core.plugin.loader;

import jframe.core.plugin.service.ServiceContext;

/**
 * @author dzh
 * @date Sep 22, 2014 10:11:42 AM
 * @since 1.1
 */
public interface PluginClassLoaderContext {

	ServiceContext getServiceContext();

	// void regPluginService(PluginCase pc);

	// void unregPluginService(PluginCase pc);

	void close();

	/**
	 * 
	 * @param clazzLoader
	 */
	void regPluginClassLoader(PluginClassLoader clazzLoader);

	/**
	 * 
	 * @param clazzLoader
	 */
	void unregPluginClassLoader(PluginClassLoader clazzLoader);

	/**
	 * 
	 * @param clazz
	 * @return
	 */
	PluginClassLoader findImportClassLoader(String clazz);

	/**
	 * 
	 * @param plugin
	 *            plug-in's name
	 * @return
	 */
	PluginClassLoader findPluginClassLoader(String plugin);

}
