/**
 * 
 */
package jframe.watch.act;

import java.util.Iterator;
import java.util.Properties;

import jframe.core.conf.Config;
import jframe.core.msg.ConfigMsg;
import jframe.core.util.ConfigUtil;
import jframe.core.util.FileUtil;
import jframe.watch.JframeWatchPlugin;

/**
 * 原来的config里有其他数据,不要直接替换掉
 * <p>
 * <li>update config.properties</li>
 * <li>send ConfigMsg</li>
 * <li>TODO 删除键如何处理</li>
 * </p>
 * 
 * @author dzh
 * @date Nov 19, 2013 3:49:39 PM
 * @since 1.0
 */
public class UpdateConfigAction implements Runnable {
	private JframeWatchPlugin plugin;
	private String config;

	public UpdateConfigAction(JframeWatchPlugin plugin, String config) {
		this.plugin = plugin;
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Properties p = new Properties();
			FileUtil.loadToProps(p, config); // TODO performance
			Config newConf = ConfigUtil.genNewConfig(config);

			Iterator<Object> iter = p.keySet().iterator();
			String key = "", oldVal = "", newVal = "";
			while (iter.hasNext()) {
				key = iter.next().toString();
				newVal = newConf.getConfig(key);
				if (newVal == null)
					continue;
				// modify config content
				oldVal = plugin.getContext().getConfig()
						.modifyConfig(key, newVal);
				// send ConfigMsg
				if (!newVal.equals(oldVal)) {
					plugin.send(ConfigMsg.createMsg(key, oldVal, newVal));
				}
			}
			newConf.clearConfig();
			plugin = null;
		} catch (Exception e) {
		}
	}

}
