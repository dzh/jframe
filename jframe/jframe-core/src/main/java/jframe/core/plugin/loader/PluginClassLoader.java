/**
 * 
 */
package jframe.core.plugin.loader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

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

	public static final Logger LOG = LoggerFactory
			.getLogger(PluginClassLoader.class);

	private PluginCase _case;

	/**
	 * @param pRef
	 */
	public PluginClassLoader(URL[] urls, PluginCase pc) {
		super(urls, pc.getClass().getClassLoader());
		this._case = pc;
		if (getParent() == null) {
			throw new NullPointerException("PluginClassLoader's parent is null");
		}
	}

	public PluginClassLoader(PluginCase pc) {
		this(new URL[] {}, pc);
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
			// load from plug-in
			try {
				c = findClass(name);
			} catch (ClassNotFoundException e1) {
				// e1.printStackTrace();
			}

			if (c == null) { // load from parent class loader
				// if not found ,throw ClassNotFoundException
				c = getParent().loadClass(name);
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
		// }
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
