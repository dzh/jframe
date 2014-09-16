/**
 * 
 */
package jframe.core.plugin;

import jframe.core.plugin.loader.PluginClassLoader;

/**
 * @author dzh
 * @date Sep 12, 2013 2:47:28 PM
 * @since 1.0
 */
public interface Plugin {

	enum PluginStatus {
		INIT, START, STOP, DESTROY, UPDATE
	}

	PluginStatus getStatus();

	// void setStatus(PluginStatus status);

	void init(PluginContext context) throws PluginException;

	void start() throws PluginException;

	void stop() throws PluginException;

	void destroy() throws PluginException;

	int getID();

	void setID(int id);

	/**
	 * v1.1
	 * 
	 * @return
	 */
	PluginContext getContext();

	String getName();

	PluginClassLoader getPluginClassLoader();

}
