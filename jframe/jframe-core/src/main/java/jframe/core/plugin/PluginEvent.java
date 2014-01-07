/**
 * 
 */
package jframe.core.plugin;

import jframe.core.plugin.Plugin.PluginStatus;

/**
 * @author dzh
 * @date Sep 12, 2013 8:08:15 PM
 * @since 1.0
 */
public class PluginEvent {

	private PluginStatus oldStatus;

	private PluginStatus newStatus;

	private Plugin plugin;

	/**
	 * @param plugin
	 */
	public PluginEvent(Plugin plugin, PluginStatus oldStatus,
			PluginStatus newStatus) {
		if (plugin == null)
			throw new IllegalArgumentException("null plugin");

		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
		this.plugin = plugin;
	}

	public PluginStatus getOldStatus() {
		return oldStatus;
	}

	public PluginStatus getNewStatus() {
		return newStatus;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public String toString() {
		return getClass().getName() + "[plugin=" + plugin.toString() + "]";
	}

}
