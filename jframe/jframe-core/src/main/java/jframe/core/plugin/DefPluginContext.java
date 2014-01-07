/**
 * 
 */
package jframe.core.plugin;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import jframe.core.conf.Config;
import jframe.core.dispatch.DefDispatchFactory;
import jframe.core.dispatch.DispatchSource;
import jframe.core.dispatch.DispatchTarget;
import jframe.core.dispatch.Dispatcher;
import jframe.core.plugin.Plugin.PluginStatus;
import jframe.core.plugin.annotation.Message;
import jframe.core.plugin.dispatch.DispatchSourceHandler;
import jframe.core.plugin.dispatch.DispatchTargetHandler;
import jframe.core.signal.Signal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 27, 2013 2:55:25 PM
 * @since 1.0
 */
public class DefPluginContext implements PluginContext, Dispatchable {

	private static final Logger _LOG = LoggerFactory
			.getLogger(DefPluginContext.class);

	private int plugin_id;

	private Config _config;

	private Map<Integer, PluginRef> _plugins; // 注册的插件

	private Collection<PluginListener> _listeners;

	private Dispatcher _dispatcher;

	public void initContext(Config config) {
		this._config = config;
		this.plugin_id = 0;
		this._plugins = new HashMap<Integer, PluginRef>();
		this._listeners = new LinkedList<PluginListener>();

		// dispatch
		_dispatcher = DefDispatchFactory.newInstance().createDispatcher(
				"PluginContextDispatcher");
	}

	public PluginRef getPlugin(int id) {
		synchronized (_plugins) {
			return _plugins.get(id);
		}
	}

	public Collection<PluginRef> getPlugins() {
		return Collections.unmodifiableCollection(_plugins.values());
	}

	public PluginRef regPlugin(Plugin plugin) {
		if (plugin == null)
			return null;
		plugin.setID(newPluginID());
		try {
			plugin.init(this);
		} catch (Exception e) {
			_LOG.error(e.getMessage());
			return null; // TODO
		}
		// reg ref before start
		PluginRef ref = null;
		synchronized (_plugins) {
			ref = containRef(plugin);
			if (ref == null) {
				ref = new DefPluginRef(this, plugin.getName());
				_plugins.put(plugin.getID(), ref);
			}
		}
		ref.setPlugin(plugin);
		// handleAnnation
		regDispatch(ref);
		// set updating false
		if (ref.isUpdating())
			ref.setUpdating(false);
		// start plugin
		try {
			plugin.start();
		} catch (PluginException e) {
			unregPlugin(plugin);
			_LOG.error("Plugin start error: " + plugin.getName()
					+ e.getLocalizedMessage());
			return null;
		}
		return ref;
	}

	/**
	 * @param plugin
	 */
	private void regDispatch(PluginRef ref) {
		Plugin plugin = ref.getPlugin();
		Message am = plugin.getClass().getAnnotation(Message.class);
		if (am == null) {
			removeDispatchSource(ref);
			removeDispatchTarget(ref);
			return;
		}

		if (am.isSender()) {
			createDispatchSource(ref);
		} else {
			removeDispatchSource(ref);
		}
		if (am.isRecver()) {
			createDispatchTarget(ref);
		} else {
			removeDispatchTarget(ref);
		}
	}

	private void createDispatchTarget(PluginRef ref) {
		Object pdt = ref.getPolicy(PluginRef.DispatchTarget);
		if (pdt == null) {
			pdt = Proxy.newProxyInstance(getClass().getClassLoader(),
					new Class[] { DispatchTarget.class },
					new DispatchTargetHandler(ref));
			ref.regPolicy(PluginRef.DispatchTarget, pdt);
			if (getDispatcher() != null)
				getDispatcher().addDispatchTarget((DispatchTarget) pdt);
		} else {
			// update previous configuration
			((DispatchTargetHandler) Proxy.getInvocationHandler(pdt))
					.update(ref.getPlugin());
		}
	}

	/**
	 * @param ref
	 */
	private void createDispatchSource(PluginRef ref) {
		Object pds = ref.getPolicy(PluginRef.DispatchSource); // null is normal
		if (pds == null) {
			pds = Proxy.newProxyInstance(getClass().getClassLoader(),
					new Class[] { DispatchSource.class },
					new DispatchSourceHandler(ref));
			ref.regPolicy(PluginRef.DispatchSource, pds);
			if (getDispatcher() != null)
				getDispatcher().addDispatchSource((DispatchSource) pds);
		}
	}

	private void removeDispatchSource(PluginRef ref) {
		Object p = ref.getPolicy(PluginRef.DispatchSource);
		Dispatcher d = getDispatcher();
		if (d != null) {
			d.removeDispatchSource((DispatchSource) p);
		}
		if (p != null) { // 清理上次的内容
			ref.unregPolicy(PluginRef.DispatchSource);
		}
	}

	private void removeDispatchTarget(PluginRef ref) {
		Object p = ref.getPolicy(PluginRef.DispatchTarget);
		Dispatcher d = getDispatcher();
		if (d != null) {
			d.removeDispatchTarget((DispatchTarget) p);
		}
		if (p != null) {
			ref.unregPolicy(PluginRef.DispatchTarget);
		}
	}

	public PluginRef unregPlugin(Plugin plugin) {
		try {
			if (plugin.getStatus() == PluginStatus.START) {
				plugin.stop();
			}
			if (plugin.getStatus() == PluginStatus.STOP) {
				plugin.destroy();
			}
		} catch (PluginException e) {
			_LOG.error(e.getLocalizedMessage());
		}

		synchronized (_plugins) {
			PluginRef ref = _plugins.get(plugin.getID());
			if (ref != null) {
				if (!ref.isUpdating()) {
					_plugins.remove(plugin.getID());
					ref.dispose();
				}
				removeDispatchSource(ref); //
				ref.setPlugin(null);
			}
			return ref;
		}
	}

	public void signal(Signal sig) {
		_config.getFrame().broadcast(sig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginContext#dispose()
	 */
	public void dispose() {
		synchronized (_plugins) {
			_plugins.clear();
		}
		synchronized (_listeners) {
			_listeners.clear();
		}
	}

	private synchronized int newPluginID() {
		return ++plugin_id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginContext#getConfig()
	 */
	public Config getConfig() {
		return _config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.plugin.PluginContext#regPluginListener(jframe.core.plugin
	 * .PluginListener)
	 */
	public void regPluginListener(PluginListener l) {
		synchronized (_listeners) {
			if (!_listeners.contains(l)) {
				_listeners.add(l);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.plugin.PluginContext#unregPluginListener(jframe.core.plugin
	 * .PluginListener)
	 */
	public void unregPluginListener(PluginListener l) {
		synchronized (_listeners) {
			_listeners.remove(l);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.plugin.PluginContext#notifyPluginEvent(jframe.core.plugin
	 * .PluginEvent)
	 */
	public void notifyPluginEvent(PluginEvent event) {
		synchronized (_listeners) {
			Iterator<PluginListener> iter = _listeners.iterator();
			while (iter.hasNext()) {
				iter.next().pluginChanged(event);
			}
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginContext#containRef(jframe.core.plugin.Plugin)
	 * @return if find PluginRef return it, or return null
	 */
	private PluginRef containRef(Plugin plugin) {
		Iterator<PluginRef> iter = _plugins.values().iterator();
		PluginRef ref = null;
		while (iter.hasNext()) {
			ref = iter.next();
			if (ref.getPluginName().equals(plugin.getName())) {
				return ref;
			}

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.IDispatchable#getDispatcher()
	 */
	public Dispatcher getDispatcher() {
		return this._dispatcher;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginContext#getPlugin(java.lang.String)
	 */
	public PluginRef getPlugin(String name) {
		synchronized (_plugins) {
			for (PluginRef pr : _plugins.values()) {
				if (pr.getPluginName().equals(name))
					return pr;
			}
		}
		return null;
	}
}
