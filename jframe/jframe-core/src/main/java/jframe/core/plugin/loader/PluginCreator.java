/**
 * 
 */
package jframe.core.plugin.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jframe.core.conf.Config;
import jframe.core.conf.ConfigConstants;
import jframe.core.plugin.Plugin;
import jframe.core.util.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 30, 2013 10:54:35 AM
 * @since 1.0
 */
public class PluginCreator {

	private static final Logger LOG = LoggerFactory
			.getLogger(PluginCreator.class);

	public static final String FILE_PLUGIN = "META-INF/plugin.properties";

	private Config _config;

	private PluginCreator(Config config) {
		this._config = config;
	}

	public static final PluginCreator newCreator(Config config) {
		return new PluginCreator(config);
	}

	public String getRootCachePath() {
		return _config.getConfig(Config.APP_CACHE);
	}

	/**
	 * 
	 * @param plugin
	 *            file
	 * @return
	 */
	public PluginCase loadPlugin(File plugin) {
		JarFile jar = null;
		InputStream is = null;
		PluginCase pc = null;
		try {
			jar = new JarFile(plugin);
			if (!isValidPlugin(jar)) {
				jar.close();
				return null;
			}

			is = jar.getInputStream(jar.getJarEntry(FILE_PLUGIN));
			Properties p = new Properties();
			p.load(is);
			is.close();
			if (isForbidden(p.getProperty(PluginCase.P_PLUGIN_NAME))) {
				LOG.info("Forbid plugin: "
						+ p.getProperty(PluginCase.P_PLUGIN_NAME));
				return null;
			}

			pc = new PluginCase();
			pc.setPluginName(p.getProperty(PluginCase.P_PLUGIN_NAME));
			LOG.info("Loading plugin "
					+ p.getProperty(PluginCase.P_PLUGIN_NAME));
			pc.setJarPath(plugin.getAbsolutePath());
			pc.setCachePath(getRootCachePath() + File.separator
					+ pc.getPluginName());
			File cache = new File(pc.getCachePath()); // init cache folder
			if (!cache.exists())
				cache.mkdirs();
			if (p.getProperty(PluginCase.P_PLUGIN_CLASS) != null) {
				pc.setPluginClass(p.getProperty(PluginCase.P_PLUGIN_CLASS));
			}
			if (p.getProperty(PluginCase.P_PLUGIN_DLL) != null) {
				File dll = new File(cache, PluginCase.DLL);
				if (!dll.exists())
					dll.mkdirs();
				List<String> dlls = new LinkedList<String>();
				for (String d : p.getProperty(PluginCase.P_PLUGIN_DLL, "")
						.split("\\s")) {
					String name = FileUtil.getName(d);
					FileUtil.copyJarEntry(jar, d, dll.getAbsolutePath()
							+ File.separator + name, false);
					dlls.add(name);
				}
				if (dlls.size() > 0)
					pc.setPluginDll(dlls);
			}
			if (p.getProperty(PluginCase.P_PLUGIN_LIB) != null) {
				File lib = new File(cache, PluginCase.LIB);
				if (!lib.exists())
					lib.mkdirs();
				List<String> libs = new LinkedList<String>();
				for (String d : p.getProperty(PluginCase.P_PLUGIN_LIB, "")
						.split("\\s")) {
					String name = FileUtil.getName(d);
					FileUtil.copyJarEntry(jar, d, lib.getAbsolutePath()
							+ File.separator + name, false);
					libs.add(name);
				}
				if (libs.size() > 0)
					pc.setPluginLib(libs);
			}
		} catch (IOException e) {
			LOG.error("Jar IO is error." + e.getMessage());
			return null;
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					LOG.warn("Plugin jar close error: "
							+ e.getLocalizedMessage());
				}
			}
		}
		return pc;
	}

	/**
	 * 
	 * @param pc
	 * @return Plugin or Null
	 */
	public Plugin createPlugin(PluginCase pc) {
		if (pc == null)
			return null;
		@SuppressWarnings("resource")
		PluginClassLoader pcl = new PluginClassLoader(pc);
		try {
			pcl.addURL(new URL("file:" + pc.getJarPath()));
			for (String lib : pc.getPluginLib()) {
				pcl.addURL(new URL("file:" + pc.getCacheLibPath()
						+ File.separator + lib));
			}
		} catch (MalformedURLException e) {
			LOG.error("Exception when create plugin:" + e.getLocalizedMessage());
			pcl.dispose();
			pcl = null;
			return null;
		}

		Plugin p = null;
		try {
			p = (Plugin) pcl.loadClass(pc.getPluginClass()).newInstance();
		} catch (Exception e) {
			LOG.error("Create Plugin Error: " + e.getLocalizedMessage());
		}
		return p;
	}

	/**
	 * 
	 * @param jar
	 * @return whether jar is a valid plugin or not
	 */
	public boolean isValidPlugin(JarFile jar) {
		JarEntry je = jar.getJarEntry(FILE_PLUGIN); //
		if (je == null)
			return false;
		InputStream is = null;
		try {
			is = jar.getInputStream(je);
			Properties p = new Properties();
			p.load(is);
			if (p.getProperty(PluginCase.P_PLUGIN_CLASS) == null
					|| p.getProperty(PluginCase.P_PLUGIN_NAME) == null) {
				return false;
			}
		} catch (IOException e) {
			LOG.warn(e.getMessage());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return true;
	}

	/**
	 * 判断一个插件是否被进制
	 * 
	 * @param pluginName
	 * @return
	 */
	public boolean isForbidden(String pluginName) {
		String[] plugins = _config.getConfig(ConfigConstants.PLUGIN_FORBID, "")
				.split(" ");
		return Arrays.asList(plugins).contains(pluginName);
	}
}
