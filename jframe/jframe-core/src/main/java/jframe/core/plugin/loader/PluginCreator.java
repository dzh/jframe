/**
 * 
 */
package jframe.core.plugin.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.conf.ConfigConstants;
import jframe.core.plugin.Plugin;
import jframe.core.plugin.loader.ext.PluginServiceCreator;
import jframe.core.util.FileUtil;

/**
 * @author dzh
 * @date Sep 30, 2013 10:54:35 AM
 * @since 1.0
 */
public class PluginCreator {

    private static final Logger LOG = LoggerFactory.getLogger(PluginCreator.class);

    public static final String FILE_PLUGIN = "META-INF/plugin.properties";

    protected Config _config;

    protected PluginCreator(Config config) {
        this._config = config;
    }

    protected PluginClassLoaderContext context;

    public static final PluginCreator newCreator(Config config, PluginClassLoaderContext context) {
        PluginCreator pc = new PluginServiceCreator(config);
        pc.context = context;
        return pc;
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
        PluginCase pc = null;
        try {
            jar = new JarFile(plugin);
            if (!isValidPlugin(jar)) {
                jar.close();
                return null;
            }

            Properties p = new Properties();
            InputStream is = null;
            try {
                is = jar.getInputStream(jar.getJarEntry(FILE_PLUGIN));
                p.load(is);
            } catch (Exception e) {
                LOG.error(e.getMessage());
                return null;
            } finally {
                if (is != null)
                    is.close();
            }

            if (isForbidden(p.getProperty(PluginCase.P_PLUGIN_NAME))) {
                LOG.info("Forbid plugin: " + p.getProperty(PluginCase.P_PLUGIN_NAME));
                return null;
            }

            pc = new PluginCase();
            pc.setPluginName(p.getProperty(PluginCase.P_PLUGIN_NAME));
            LOG.info("Loading plugin " + p.getProperty(PluginCase.P_PLUGIN_NAME));
            pc.setJarPath(plugin.getAbsolutePath());
            pc.setCachePath(getRootCachePath() + File.separator + pc.getPluginName());
            File cache = new File(pc.getCachePath()); // init cache folder
            if (!cache.exists())
                cache.mkdirs();
            if (p.getProperty(PluginCase.P_PLUGIN_CLASS) != null) {
                pc.setPluginClass(p.getProperty(PluginCase.P_PLUGIN_CLASS));
            }
            // TODO support *
            if (p.getProperty(PluginCase.P_PLUGIN_DLL) != null) {
                File dll = new File(cache, PluginCase.DLL);
                if (!dll.exists())
                    dll.mkdirs();
                List<String> dlls = new LinkedList<String>();
                for (String d : p.getProperty(PluginCase.P_PLUGIN_DLL, "").split("\\s+")) {
                    String name = FileUtil.getLastName(d);
                    FileUtil.copyJarEntry(jar, d, dll.getAbsolutePath() + File.separator + name, false);
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
                for (String d : p.getProperty(PluginCase.P_PLUGIN_LIB, "").split("\\s+")) {
                    String name = FileUtil.getLastName(d);
                    FileUtil.copyJarEntry(jar, d, lib.getAbsolutePath() + File.separator + name, false);
                    libs.add(name);
                }
                if (libs.size() > 0)
                    pc.setPluginLib(libs);
            }

            loadPlugin(pc, p);
        } catch (IOException e) {
            LOG.error("Jar IO is error." + e.getMessage());
            return null;
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                    LOG.warn("Plugin jar close error: " + e.getLocalizedMessage());
                }
            }
        }
        return pc;
    }

    protected void loadPlugin(PluginCase pc, Properties p) {
        // parse import-plugin
        if (p.getProperty(PluginCase.P_IMPORT_PLUGIN) != null) {
            pc.setImportPlugin(parseList(p.getProperty(PluginCase.P_IMPORT_PLUGIN)));
        }
        if (p.getProperty(PluginCase.P_IMPORT_CLASS) != null) {
            pc.setImportClass(parseList(p.getProperty(PluginCase.P_IMPORT_CLASS)));
        }
        if (p.getProperty(PluginCase.P_EXPORT_CLASS) != null) {
            pc.setExportClass(parseList(p.getProperty(PluginCase.P_EXPORT_CLASS)));
        }
    }

    protected List<String> parseList(String pValue) {
        if (pValue == null || "".equals(pValue))
            return Collections.emptyList();
        return Arrays.asList(pValue.split("\\s+"));
    }

    /**
     * 
     * @param pc
     * @return Plugin or Null
     */
    public Plugin createPlugin(PluginClassLoader pcl, PluginCase pc) {
        if (pcl == null || pc == null)
            return null;
        return pcl.createPlugin(pc);
    }

    public PluginClassLoader createPluginClassLoader(PluginCase pc) {
        PluginClassLoader pcl = new PluginClassLoader(pc, context);

        return pcl;
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
            if (p.getProperty(PluginCase.P_PLUGIN_CLASS) == null || p.getProperty(PluginCase.P_PLUGIN_NAME) == null) {
                return false;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage());
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
        String[] plugins = _config.getConfig(ConfigConstants.PLUGIN_FORBID, "").split(" ");
        return Arrays.asList(plugins).contains(pluginName);
    }

    /**
     * 
     */
    public void close() {
        _config = null;
    }
}
