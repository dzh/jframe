/**
 * 
 */
package jframe.core.unit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.List;

import jframe.core.Frame;
import jframe.core.conf.Config;
import jframe.core.plugin.DefPluginContext;
import jframe.core.plugin.PluginContext;
import jframe.core.plugin.loader.PluginCase;
import jframe.core.signal.Signal;

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

	private WatchService _watcher;

	private List<PluginCase> _plugins;

	private PluginContext _context;

	@Override
	public void init(Frame frame) {
		super.init(frame);
		_context = new DefPluginContext();
		_context.initContext(this); // TODO conf?

		_plugins = new LinkedList<PluginCase>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.unit.Unit#start()
	 */
	@Override
	public void start() throws UnitException {
		String path_plugin = getFrame().getConfig()
				.getConfig(Config.APP_PLUGIN);
		// 1.load plug-in
		doLoadPlugin(path_plugin);
		// 2.monitor plug-in directory
		//doMonitorPlugin(path_plugin);
	}

	/**
	 * @param path_plugin
	 */
	private void doLoadPlugin(String path_plugin) {
		File dir_plugin = Paths.get(path_plugin).toFile();
		dir_plugin.mkdirs();
		File[] plugins = dir_plugin.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		for (File f : plugins) {
			if (!validatePluginFile(f))
				continue;

		}
	}

	/**
	 * 
	 * @param f
	 * @return
	 */
	private boolean validatePluginFile(File f) {

		return false;
	}

	protected void cachePluginJar() {

	}

	/**
	 * 
	 */
	private void doMonitorPlugin(String path_plugin) throws UnitException {
		final Path app_plugin = Paths.get(path_plugin);
		try {
			_watcher = FileSystems.getDefault().newWatchService();
			app_plugin.register(_watcher, ENTRY_CREATE, ENTRY_MODIFY,
					ENTRY_DELETE);
		} catch (IOException e1) {
			throw new UnitException("Create Plugin WatchService Error: "
					+ e1.getMessage());
		}

		new Thread() {
			public void run() {
				WatchKey key = null;
				for (;;) {
					try {
						key = _watcher.take();
					} catch (InterruptedException e) {
						LOG.error("WatchService is interrupted. Exception: "
								+ e.getLocalizedMessage());
						break;
					} catch (ClosedWatchServiceException e) {
						LOG.info("WatchService is closed.");
						break;
					}
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();
						if (kind == OVERFLOW)
							continue;
						Path jarFile = (Path) event.context();
						if (kind == ENTRY_CREATE) { // TODO

						} else if (kind == ENTRY_MODIFY) {

						} else if (kind == ENTRY_DELETE) {

						}
						app_plugin.resolve(jarFile);
					}
					boolean valid = key.reset();
					if (!valid) {
						LOG.info("WatchKey is invalid: " + key.toString());
						break;
					}
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.unit.Unit#stop()
	 */
	@Override
	public void stop() throws UnitException {
		try {
			if (_watcher != null)
				_watcher.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		if (_context != null)
			_context.dispose();
		if (_plugins != null)
			_plugins.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.unit.Unit#recvSig(jframe.core.signal.Signal)
	 */
	@Override
	public void recvSig(Signal sig) {

	}

}
