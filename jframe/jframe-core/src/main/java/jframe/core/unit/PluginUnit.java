/**
 * 
 */
package jframe.core.unit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import jframe.core.Frame;
import jframe.core.conf.Config;
import jframe.core.plugin.DefPluginContext;
import jframe.core.plugin.Plugin;
import jframe.core.plugin.PluginContext;
import jframe.core.plugin.PluginRef;
import jframe.core.plugin.loader.PluginCase;
import jframe.core.plugin.loader.PluginCreator;
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

	private PluginCreator _creator; // create plugin

	@Override
	public void init(Frame frame) throws UnitException {
		super.init(frame);
		_context = new DefPluginContext();
		_context.initContext(frame.getConfig());

		_creator = PluginCreator.newCreator(frame.getConfig());
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
		regPlugin(loadPlugin());
	}

	/**
	 * 
	 */
	private void regPlugin(Collection<Plugin> plugins) {
		if (plugins == null || plugins.size() == 0)
			return;

		TreeSet<Plugin> set = new TreeSet<Plugin>(new PluginComparator(
				PluginComparator.TYPE.START));
		for (Plugin p : plugins) {
			if (!set.add(p))
				LOG.warn("Found repeated plugin: " + p.getName());
		}
		Iterator<Plugin> iter = set.iterator();
		Plugin p = null;
		while (iter.hasNext()) {
			p = iter.next();
			try {
				_context.regPlugin(p);
			} catch (Exception e) { //TODO 
				LOG.error("When invoke pluginUnit.regPlugin(), plugin name is "
						+ p.getName() + " " + e.getMessage());
				_context.unregPlugin(p);
			}
			iter.remove();
		}
	}

	/**
	 * @param plugins
	 */
	private void unregPlugin(Collection<PluginRef> plugins) {
		if (plugins.size() == 0)
			return;

		TreeSet<Plugin> set = new TreeSet<Plugin>(new PluginComparator(
				PluginComparator.TYPE.STOP));
		for (PluginRef ref : plugins) {
			if (ref.getPlugin() != null && !set.add(ref.getPlugin()))
				LOG.warn("Found repeated plugin: " + ref.getPlugin().getName());
		}
		Iterator<Plugin> iter = set.iterator();
		while (iter.hasNext()) {
			_context.unregPlugin(iter.next());
		}
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
		List<Plugin> pluginList = new LinkedList<Plugin>();

		String path_plugin = getFrame().getConfig()
				.getConfig(Config.APP_PLUGIN);
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
		return pluginList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.unit.Unit#stop()
	 */
	public void stop() throws UnitException {
		// stop plugins
		unregPlugin(_context.getPlugins());

		if (_context != null)
			_context.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.unit.Unit#recvSig(jframe.core.signal.Signal)
	 */
	public void recvSig(Signal sig) {
		// TODO
	}

	public static class PluginComparator implements Comparator<Plugin> {

		public static enum TYPE {
			START, STOP
		}

		private TYPE type;

		public PluginComparator(TYPE type) {
			this.type = type;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Plugin o1, Plugin o2) {
			int v = 0;
			jframe.core.plugin.annotation.Plugin ap1 = o1.getClass()
					.getAnnotation(jframe.core.plugin.annotation.Plugin.class);
			jframe.core.plugin.annotation.Plugin ap2 = o2.getClass()
					.getAnnotation(jframe.core.plugin.annotation.Plugin.class);
			switch (type) {
			case START: {
				v = minus(ap1.startOrder(), ap2.startOrder());
				break;
			}
			case STOP: {
				v = minus(ap1.stopOrder(), ap2.stopOrder());
				break;
			}
			}

			if (v == 0) {
				v = o1.getName().compareTo(o2.getName());
			}
			return v;
		}

		/**
		 * 负数作为最大整数
		 * 
		 * @param minuend
		 * @param subtrahend
		 * @return
		 */
		int minus(int minuend, int subtrahend) {
			if (minuend < 0) {
				minuend = Integer.MAX_VALUE;
			}
			if (subtrahend < 0) {
				subtrahend = Integer.MAX_VALUE;
			}
			return minuend - subtrahend;
		}
	}

}
