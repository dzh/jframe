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
import jframe.core.plugin.annotation.Injector;

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

	protected PluginClassLoaderContext plc;

	/**
	 * @param pRef
	 */
	public PluginClassLoader(URL[] urls, PluginCase pc,
			PluginClassLoaderContext plc) {
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

		this.plc = plc;
		this.plc.regPluginClassLoader(this);
	}

	public PluginClassLoaderContext getPluginClassLoaderContext() {
		return plc;
	}

	public PluginClassLoader(PluginCase pc, PluginClassLoaderContext plc) {
		this(new URL[] {}, pc, plc);
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
			// load import class
			if (getPluginCase().getImportClass().contains(name)) {
				PluginClassLoader pcl = getPluginClassLoaderContext()
						.findImportClassLoader(name);
				if (pcl != null)
					return pcl.loadClass(name);
			}
			// load plug-in class
			try {
				c = findPluginClass(name);
				if (isInjector(c))
					injectAnnocation(c);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}

		if (c == null) {
			try {
				c = getParent().loadClass(name);
			} catch (ClassNotFoundException e) {
				c = loadImportPlugin(name);
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
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
	protected Class<? extends Plugin> loadPluginClass(PluginCase pc)
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
	protected Class<?> findPluginClass(String name) {
		Class<?> c = null;
		try {
			// load from plug-in
			c = findClass(name);
		} catch (ClassNotFoundException e) {
		}
		return c;
	}

	/**
	 * load class from import-plugin
	 * 
	 * @param name
	 * @return
	 */
	protected Class<?> loadImportPlugin(String name) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("LoadImportPlugin -> {}", name);
		}

		Class<?> clazz = null;
		for (String plugin : getPluginCase().getImportPlugin()) {
			try {
				PluginClassLoader pcl = plc.findPluginClassLoader(plugin);
				if (pcl == null) // TODO
					continue;
				clazz = pcl.loadClass(name);
				break;
			} catch (ClassNotFoundException e) {
			}
		}
		return clazz;
	}

	protected boolean isInjector(Class<?> clazz) {
		try {
			return clazz != null && clazz.isAnnotationPresent(Injector.class) ? true
					: false;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 
	 * @param clazz
	 * @throws Exception
	 */
	protected void injectAnnocation(Class<?> clazz) throws Exception {
		if (clazz == null)
			return;

		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())
					&& f.isAnnotationPresent(InjectPlugin.class)) {
				injectPlugin(f);
				break;
			}
		}
	}

	protected void injectPlugin(Field f) {
		try {
			f.setAccessible(true);
			f.set(null, getPlugin());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("InjectPlugin {} -> {}", getPlugin(), f.getName());
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
		plc.unregPluginClassLoader(this);
	}
}
