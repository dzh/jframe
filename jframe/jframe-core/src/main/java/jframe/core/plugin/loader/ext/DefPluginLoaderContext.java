package jframe.core.plugin.loader.ext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jframe.core.plugin.loader.PluginCase;
import jframe.core.plugin.loader.PluginClassLoader;
import jframe.core.plugin.loader.PluginClassLoaderContext;
import jframe.core.plugin.service.ServiceContext;

/**
 * <p>
 * <li>service finding</li>
 * <li></li>
 * </p>
 * 
 * @author dzh
 * @date Sep 15, 2014 5:11:00 PM
 * @since 1.1
 */
public class DefPluginLoaderContext implements PluginClassLoaderContext {

	private ServiceContext _sc;

	/**
	 * TODO local storage
	 * <p>
	 * <pluginName,pluginClassLoader>
	 * </p>
	 */
	private Map<String, PluginClassLoader> clazzLoaders = Collections
			.synchronizedMap(new HashMap<String, PluginClassLoader>());

	/**
	 * <clazz,pluginName>
	 */
	private Map<String, String> exportClazz = Collections
			.synchronizedMap(new HashMap<String, String>());

	public DefPluginLoaderContext() {
		_sc = new ServiceContext();
	}

	public ServiceContext getServiceContext() {
		return _sc;
	}

	public void close() {
		_sc.close();
		clazzLoaders.clear();
		exportClazz.clear();
	}

	public PluginClassLoader findPluginClassLoader(String name) {
		return clazzLoaders.get(name);
	}

	/**
	 * 
	 * @param name
	 * @param clazzLoader
	 */
	protected void putPluginClassLoader(String name,
			PluginClassLoader clazzLoader) {
		if (!clazzLoaders.containsKey(name)) {
			clazzLoaders.put(name, clazzLoader);
		}
	}

	protected void removePluginClassLoader(String name) {
		if (clazzLoaders.containsKey(name)) {
			clazzLoaders.remove(name);
		}
	}

	/**
	 * 
	 * @param clazz
	 *            export/import class
	 * @param name
	 *            plugin's name
	 */
	protected void putExportClass(String clazz, String name) {
		exportClazz.put(clazz, name);
	}

	protected void removeExportClass(String clazz) {
		exportClazz.remove(clazz);
	}

	/**
	 * @param clazz
	 *            export/import class
	 * @return
	 */
	public PluginClassLoader findImportClassLoader(String clazz) {
		return clazzLoaders.get(exportClazz.get(clazz));
	}

	/**
	 * register plugin
	 */
	public void regPluginClassLoader(PluginClassLoader clazzLoader) {
		PluginCase pc = clazzLoader.getPluginCase();
		// plugin name
		putPluginClassLoader(pc.getPluginName(), clazzLoader);
		// export class
		for (String clazz : pc.getExportClass()) {
			putExportClass(clazz, pc.getPluginName());
		}
		// plugin-service is exported default
		for (String clazz : pc.getPluginService()) {
			putExportClass(clazz, pc.getPluginName());
		}

	}

	/**
	 * unregister plugin
	 */
	public void unregPluginClassLoader(PluginClassLoader clazzLoader) {
		PluginCase pc = clazzLoader.getPluginCase();
		for (String clazz : pc.getExportClass()) {
			removeExportClass(clazz);
		}
		for (String clazz : pc.getPluginService()) {
			removeExportClass(clazz);
		}
		removePluginClassLoader(clazzLoader.getPluginCase().getPluginName());
	}

}
