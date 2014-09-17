/**
 * 
 */
package jframe.core.plugin.loader;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import jframe.core.plugin.Plugin;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.loader.ext.PluginLoaderContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 功能:
 * <li>插件类加载</li>
 * <li>动态库加载</li>
 * </P>
 * 
 * @author dzh
 * @date Sep 12, 2013 1:26:44 PM
 * @since 1.0
 */
public class PluginClassLoader extends URLClassLoader {

	private static final Logger LOG = LoggerFactory
			.getLogger(PluginClassLoader.class);

	private PluginCase _case;

	protected PluginLoaderContext plc;

	/**
	 * @param pRef
	 */
	public PluginClassLoader(URL[] urls, PluginCase pc) {
		super(urls, pc.getClass().getClassLoader());
		this._case = pc;
		if (getParent() == null) {
			throw new NullPointerException("PluginClassLoader's parent is null");
		}
		try {
			addURL(new URL("file:" + pc.getJarPath()));
			for (String lib : pc.getPluginLib()) {
				addURL(new URL("file:" + pc.getCacheLibPath() + File.separator
						+ lib));
			}
		} catch (MalformedURLException e) {
			LOG.error("Exception when create plugin:" + e.getLocalizedMessage());
			dispose();
		}
	}

	public PluginClassLoader(PluginCase pc, PluginLoaderContext plc) {
		this(new URL[] {}, pc);
		this.plc = plc;

	}

	public void addURL(URL url) {
		super.addURL(url);
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		// since 1.7
		// synchronized (getClassLoadingLock(name)) {
		// get loaded class
		Class<?> c = findLoadedClass(name);

		if (c == null) {
			c = loadLocalPlugin(name);
		}
		if (c == null)
			throw new ClassNotFoundException(name);

		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	private Plugin _plugin;

	protected synchronized Plugin createPlugin(PluginCase pc) {
		if (_plugin != null)
			return _plugin;
		try {
			_plugin = (Plugin) loadClass(pc.getPluginClass()).newInstance();
		} catch (Exception e) {
			LOG.error("Create Plugin Error: " + e.getLocalizedMessage());
		}
		return _plugin;
	}

	@SuppressWarnings("unchecked")
	protected Class<? extends Plugin> loadPlugin(PluginCase pc)
			throws ClassNotFoundException {
		return (Class<? extends Plugin>) loadClass(pc.getPluginClass());
	}

	public synchronized Plugin getPlugin() {
		if (_plugin == null) {
			createPlugin(getPluginCase());
		}
		return _plugin;
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws ClassNotFoundException
	 */
	protected Class<?> loadLocalPlugin(String name)
			throws ClassNotFoundException {
		Class<?> c = null;
		try {
			// load from plug-in
			c = findClass(name);
			injectService(c);
		} catch (ClassNotFoundException e) {
			c = getParent().loadClass(name);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return c;
	}

	protected void injectService(Class<?> clazz) throws Exception {
		if (clazz == null)
			return;

		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())
					&& f.isAnnotationPresent(InjectPlugin.class)) {
				try {
					f.setAccessible(true);
					f.set(null, getPlugin());
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
				break;
			}
		}
	}

	@Override
	protected String findLibrary(String libname) {
		String path = _case.getCacheDllPath() + File.separator + libname;
		if (new File(path).exists())
			return path;
		return null;
	}

	/**
	 * <p>
	 * 查询次序:
	 * <li>相對插件的cache目录</li>
	 * <li>绝对路径</li>
	 * <li>相對jar包</li>
	 * </p>
	 */
	@Override
	public URL findResource(String name) {
		try {
			File f = new File(_case.getCachePath() + File.separator + name);
			if (f.exists())
				return f.toURI().toURL();
		} catch (MalformedURLException e) {
			LOG.warn(e.getLocalizedMessage());
		}

		try {
			File f = new File(name);
			if (f.exists())
				return f.toURI().toURL();
		} catch (MalformedURLException e) {
			LOG.warn(e.getLocalizedMessage());
		}

		return super.findResource(name);
	}

	public PluginCase getPluginCase() {
		return _case;
	}

	/**
	 * jdk1.7之后调用close
	 */
	public void dispose() {
		// _case = null;
		Method m = null;
		try {
			m = getClass().getMethod("close", new Class[0]);
			if (m != null) {
				m.invoke(this, new Object[0]);
			}
		} catch (Exception e) {
			LOG.warn("Exception when PluginClassLoader close():"
					+ e.getMessage());
		}
	}
}
