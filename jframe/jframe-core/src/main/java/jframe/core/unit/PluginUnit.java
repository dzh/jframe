/**
 * 
 */
package jframe.core.unit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.Frame;
import jframe.core.conf.Config;
import jframe.core.plugin.DefPluginContext;
import jframe.core.plugin.Plugin;
import jframe.core.plugin.PluginContext;
import jframe.core.plugin.loader.PluginCase;
import jframe.core.plugin.loader.PluginClassLoader;
import jframe.core.plugin.loader.PluginClassLoaderContext;
import jframe.core.plugin.loader.PluginCreator;
import jframe.core.plugin.loader.ext.DefPluginLoaderContext;
import jframe.core.plugin.loader.ext.PluginServiceClassLoader;
import jframe.core.plugin.loader.ext.PluginServiceCreator;
import jframe.core.signal.Signal;
import jframe.core.util.FileUtil;

/**
 * <p>
 * Feature:
 * <li>load plug-in</li>
 * </p>
 * 
 * @author dzh
 * @date Sep 23, 2013 2:47:41 PM
 * @since 1.0
 */
public class PluginUnit extends AbstractUnit {

    private static final Logger LOG = LoggerFactory.getLogger(PluginUnit.class);

    private PluginContext _context;

    private PluginClassLoaderContext _plc;

    public PluginUnit() {
        setName(PluginUnit.class.getSimpleName());
    }

    @Override
    public void init(Frame frame) throws UnitException {
        super.init(frame);
        _context = new DefPluginContext();
        _context.initContext(frame.getConfig());

        _plc = new DefPluginLoaderContext();
    }

    public PluginClassLoaderContext getLoaderContext() {
        return _plc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.unit.Unit#start()
     */
    public void start() throws UnitException {
        // clean cache
        cleanCache();
        // load then register plug-in
        _context.regPlugins(loadPlugin(), null);
    }

    /**
     * 
     */
    private void cleanCache() {
        Config config = getFrame().getConfig();
        if ("true".equalsIgnoreCase(config.getConfig(Config.ARG_CLEAN))) {
            LOG.info("Start cleaning cache directory");
            File file = new File(config.getConfig(Config.APP_CACHE));
            FileUtil.deleteAll(file);
            file.mkdirs();
            LOG.info("Finish cleaning!");
        }
    }

    /**
     * 
     * @param path_plugin
     */
    private List<Plugin> loadPlugin() {
        // create plugin
        PluginCreator _creator = PluginCreator.newCreator(_context.getConfig(), _plc);

        List<Plugin> pluginList = new LinkedList<Plugin>();
        try {
            String path_plugin = getFrame().getConfig().getConfig(Config.APP_PLUGIN);
            File dir_plugin = new File(path_plugin);// plug-in root directory
            if (!dir_plugin.exists()) {
                LOG.warn("Not exist plugin path {}", path_plugin);
                return pluginList;
            }
            File[] plugins = dir_plugin.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });

            // Plugin p = null;
            Map<PluginCase, PluginClassLoader> pcMap = new HashMap<PluginCase, PluginClassLoader>();
            for (File f : plugins) {
                PluginCase pc = _creator.loadPlugin(f);
                if (pc != null) {
                    pcMap.put(pc, _creator.createPluginClassLoader(pc));
                }
            }
            // load service
            if (_creator instanceof PluginServiceCreator) {
                for (PluginCase pc : pcMap.keySet()) {
                    ((PluginServiceClassLoader) pcMap.get(pc)).loadService(pc);
                }
            }
            // create plugin
            for (PluginCase pc : pcMap.keySet()) {
                Plugin p = _creator.createPlugin(pcMap.get(pc), pc);
                if (p != null)
                    pluginList.add(p);
            }
        } finally {
            _creator.close();
        }
        return pluginList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.unit.Unit#stop()
     */
    public void stop() throws UnitException {
        if (_context != null) {
            // stop plugins
            _context.dispose();
        }
        if (_plc != null) {
            _plc.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.unit.Unit#recvSig(jframe.core.signal.Signal)
     */
    public void recvSig(Signal sig) {
        // TODO
    }

}
