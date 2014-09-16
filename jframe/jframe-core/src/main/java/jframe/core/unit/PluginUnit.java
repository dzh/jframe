/**
 * 
 */
package jframe.core.unit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

import jframe.core.Frame;
import jframe.core.conf.Config;
import jframe.core.plugin.DefPluginContext;
import jframe.core.plugin.Plugin;
import jframe.core.plugin.PluginContext;
import jframe.core.plugin.loader.PluginCase;
import jframe.core.plugin.loader.PluginCreator;
import jframe.core.plugin.loader.ext.PluginLoaderContext;
import jframe.core.plugin.loader.ext.PluginServiceCreator;
import jframe.core.signal.Signal;
import jframe.core.util.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private PluginLoaderContext _plc;

	@Override
	public void init(Frame frame) throws UnitException {
		super.init(frame);
		_context = new DefPluginContext();
		_context.initContext(frame.getConfig());
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
			LOG.info("Finish clean!");
		}
	}

	/**
	 * 
	 * @param path_plugin
	 */
	private List<Plugin> loadPlugin() {
		// create plugin
		PluginCreator _creator = PluginCreator.newCreator(_context.getConfig());
		if (_creator instanceof PluginServiceCreator) {
			_plc = ((PluginServiceCreator) _creator).getLoaderContext();
		}

		List<Plugin> pluginList = new LinkedList<Plugin>();

		try {
			String path_plugin = getFrame().getConfig().getConfig(
					Config.APP_PLUGIN);
			File dir_plugin = new File(path_plugin);// plug-in root directory
			if (!dir_plugin.exists()) {
				LOG.error("Not found plugin path " + path_plugin);
				return pluginList;
			}
			File[] plugins = dir_plugin.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			});

			PluginCase pc = null;
			Plugin p = null;
			for (File f : plugins) {
				pc = _creator.loadPlugin(f);
				if (pc != null) {
					p = _creator.createPlugin(pc);
					if (p != null)
						pluginList.add(p);
				}
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
