/**
 * 
 */
package jframe.watch;

import java.io.IOException;

import jframe.core.conf.Config;
import jframe.core.msg.ConfigMsg;
import jframe.core.plugin.PluginContext;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginSender;
import jframe.watch.act.UpdateConfigAction;
import name.pachler.nio.file.FileSystems;
import name.pachler.nio.file.Path;
import name.pachler.nio.file.Paths;
import name.pachler.nio.file.StandardWatchEventKind;
import name.pachler.nio.file.WatchEvent;
import name.pachler.nio.file.WatchKey;
import name.pachler.nio.file.WatchService;

/**
 * <p>
 * <li>TODO 其他文件处理</li>
 * <li>TODO 提供配置定义</li>
 * </p>
 * 
 * @author dzh
 * @date Nov 19, 2013 2:35:45 PM
 * @since 1.0
 */
public class JframeWatchPlugin extends PluginSender {

	private WatchService _watcher;

	// private static final String WATCH_PATH = "watch.path";
	// private static final String WATCH_FILE = "watch.file";
	// private static final String WATCH_FILE_SUFFIX = "watch.file.suffix";
	// private static final String SEPR = " "; // 属性值之间的分格符

	public void init(PluginContext context) throws PluginException {
		super.init(context);
	}

	public void start() throws PluginException {
		new Thread("JframeWatch") {
			public void run() {
				try {
					doWatch();
				} catch (Exception e) {
					logError(e.getMessage());
				}
			}

			private void doWatch() throws Exception {
				// default watcher
				_watcher = FileSystems.getDefault().newWatchService();
				// register default conf home
				Path path = Paths.get(getConfig(Config.APP_CONF));
				path.register(_watcher, StandardWatchEventKind.ENTRY_CREATE,
						StandardWatchEventKind.ENTRY_MODIFY);
				// register watch.path if exists TODO use anothor watcher
				// String[] watchPaths = getConfig(WATCH_PATH, "").split(SEPR);
				// for (String p : watchPaths) {
				// Paths.get(p.trim()).register(_watcher,
				// StandardWatchEventKind.ENTRY_CREATE,
				// StandardWatchEventKind.ENTRY_MODIFY);
				// }

				// plugin
				// path = Paths.get(getConfig(Config.APP_PLUGIN));
				// path.register(_watcher, StandardWatchEventKind.ENTRY_CREATE,
				// StandardWatchEventKind.ENTRY_MODIFY);
				// polling
				WatchKey key = null;
				for (;;) {
					try {
						key = _watcher.take();
					} catch (InterruptedException e) {
						break;
					}

					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();

						if (kind == StandardWatchEventKind.OVERFLOW)
							continue;
						@SuppressWarnings("unchecked")
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						handleWatchEvent(ev);
					}
					key.reset();
				}
			}

			private void handleWatchEvent(WatchEvent<Path> ev) {
				String path = ev.context().toString();
				if (Config.FILE_CONFIG.equals(path)) {
					new Thread(new UpdateConfigAction(JframeWatchPlugin.this,
							getConfig(Config.FILE_CONFIG))).start();
					return;
				}

				// new Thread(new UpdatePluginActioin()).start();
				// String[] suffix = getConfig(WATCH_FILE_SUFFIX,
				// "").split(SEPR);
				// for (String suf : suffix) {
				// if (path.endsWith(suf)) {
				// // send
				// JframeWatchPlugin.this.send();
				// return;
				// }
				// }

				// 默认处理 TODO
				JframeWatchPlugin.this.send(ConfigMsg.createMsg(Paths.get(
						getConfig(Config.APP_CONF)).toString()
						+ "/" + path));
			}
		}.start();
		super.start();
	}

	// private TextMsg createWatchMsg(String file){
	// TextMsg msg = new TextMsg();
	// msg.setMeta("watch", value)
	// }

	public void stop() throws PluginException {
		try {
			if (_watcher != null)
				_watcher.close();
		} catch (IOException e) {
			logWarn(e.getMessage());
		}
		super.stop();
	}

	public void destroy() throws PluginException {
		super.destroy();
	}

}
