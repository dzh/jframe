/**
 * 
 */
package jframe.core.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dzh
 * @date Oct 8, 2013 11:06:14 PM
 * @since 1.0
 */
public class DefPluginRef implements PluginRef {

	private PluginContext _context;

	private String _name;

	private Plugin _plugin;

	private volatile boolean _updating;

	private Map<String, Object> _policies;

	/**
	 * @param _context
	 * @param _name
	 * @param _plugin
	 * @param _updating
	 */
	public DefPluginRef(PluginContext context, String name, Plugin plugin,
			boolean updating) {
		this._context = context;
		this._name = name;
		this._plugin = plugin;
		this._updating = updating;

		if (_policies == null) {
			_policies = new HashMap<String, Object>();
		}
	}

	public DefPluginRef(PluginContext context, String name) {
		this(context, name, null, false);
	}

	/**
	 * @param defPluginContext
	 * @param name
	 * @param plugin
	 */
	public DefPluginRef(PluginContext context, String name, Plugin plugin) {
		this(context, name, plugin, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#getContext()
	 */
	public PluginContext getContext() {
		return _context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#getPlugin()
	 */
	public Plugin getPlugin() {
		return _plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#getPluginName()
	 */
	public String getPluginName() {
		return _name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#isUpdating()
	 */
	public boolean isUpdating() {
		return this._updating;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#setUpdating(boolean)
	 */
	public void setUpdating(boolean u) {
		this._updating = u;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#setPlugin(jframe.core.plugin.Plugin)
	 */
	public void setPlugin(Plugin plugin) {
		this._plugin = plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#regPolicy(java.lang.String,
	 * java.lang.Object)
	 */
	public void regPolicy(String name, Object policy) {
		synchronized (_policies) {
			_policies.put(name, policy);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#unregPolicy(java.lang.String)
	 */
	public void unregPolicy(String name) {
		synchronized (_policies) {
			_policies.remove(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#getPolicy(java.lang.String)
	 */
	public Object getPolicy(String name) {
		if (_policies == null)
			return null;
		synchronized (_policies) {
			return _policies.get(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRef#dispose()
	 */
	public void dispose() {
		synchronized (_policies) {
			_policies.clear();
		}
	}

}
