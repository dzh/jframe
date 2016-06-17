/**
 * 
 */
package jframe.core.plugin;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.conf.ConfigConstants;
import jframe.core.dispatch.DefDispatcher;
import jframe.core.dispatch.DispatchSource;
import jframe.core.dispatch.DispatchTarget;
import jframe.core.dispatch.Dispatcher;
import jframe.core.plugin.Plugin.PluginStatus;
import jframe.core.plugin.annotation.Message;
import jframe.core.plugin.dispatch.DispatchSourceHandler;
import jframe.core.plugin.dispatch.DispatchTargetHandler;
import jframe.core.signal.Signal;

/**
 * @author dzh
 * @date Sep 27, 2013 2:55:25 PM
 * @since 1.0
 */
public class DefPluginContext implements PluginContext, Dispatchable {

    private static final Logger _LOG = LoggerFactory.getLogger(DefPluginContext.class);

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
        if (_dispatcher == null) {
            _dispatcher = createDefaultDispatcher();
            _dispatcher.start();
        }
    }

    private Dispatcher createDefaultDispatcher() {
        Dispatcher d = null;
        try {
            Class<?> clazz = Class
                    .forName(_config.getConfig(ConfigConstants.CONTEXT_DISPATCHER, DefDispatcher.class.getName()));
            d = (Dispatcher) clazz.getConstructor(String.class, Config.class).newInstance(
                    _config.getConfig(ConfigConstants.CONTEXT_DISPATCHER_ID, "PluginContextDispatcher"), _config);
        } catch (Exception e) {
            _LOG.warn(e.getMessage(), e.fillInStackTrace());
            
            d = DefDispatcher.newDispatcher("PluginContextDispatcher", getConfig());
        }
        return d;
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
        } catch (Exception e) {
            unregPlugin(plugin);
            _LOG.error("Plugin start error: " + plugin.getName() + e.getLocalizedMessage());
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
            pdt = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { DispatchTarget.class },
                    new DispatchTargetHandler(ref));
            ref.regPolicy(PluginRef.DispatchTarget, pdt);
            if (getDispatcher() != null)
                getDispatcher().addDispatchTarget((DispatchTarget) pdt);
        } else {
            // update previous configuration
            ((DispatchTargetHandler) Proxy.getInvocationHandler(pdt)).update(ref.getPlugin());
        }
    }

    /**
     * @param ref
     */
    private void createDispatchSource(PluginRef ref) {
        Object pds = ref.getPolicy(PluginRef.DispatchSource); // null is normal
        if (pds == null) {
            pds = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { DispatchSource.class },
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
            _LOG.error(e.getMessage());
        }

        synchronized (_plugins) {
            PluginRef ref = _plugins.get(plugin.getID());
            if (ref != null) {
                if (!ref.isUpdating()) {
                    removeDispatchTarget(ref);
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

    /**
     * <p>
     * 关闭过程：
     * <li>关闭插件</li>
     * <li>关闭dispatch</li>
     * </p>
     * (non-Javadoc)
     * 
     * @see jframe.core.plugin.PluginContext#dispose()
     */
    public void dispose() {
        unregPlugins(getPlugins(), null);
        _dispatcher.close();

        synchronized (_plugins) {
            _plugins.clear();
        }
        synchronized (_listeners) {
            _listeners.clear();
        }
    }

    private int newPluginID() {
        synchronized (this) {
            return ++plugin_id;
        }
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

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.plugin.PluginContext#regPlugins(java.util.Collection,
     * java.util.Comparator)
     */
    public void regPlugins(Collection<Plugin> plugins, Comparator<? super Plugin> comparator) {
        if (plugins == null || plugins.size() == 0)
            return;

        if (comparator == null) {
            comparator = new PluginComparator(PluginComparator.TYPE.START);
        }
        TreeSet<Plugin> set = new TreeSet<Plugin>(comparator);
        for (Plugin p : plugins) {
            if (!set.add(p))
                _LOG.warn("Found repeated plugin: " + p.getName());
        }
        Iterator<Plugin> iter = set.iterator();
        Plugin p = null;
        while (iter.hasNext()) {
            p = iter.next();
            try {
                regPlugin(p);
            } catch (Exception e) {
                _LOG.error(
                        "When invoke pluginContext.regPlugin(), plugin name is " + p.getName() + " " + e.getMessage());
                unregPlugin(p);
            }
            iter.remove();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.plugin.PluginContext#unregPlugins(java.util.Collection,
     * java.util.Comparator)
     */
    public void unregPlugins(Collection<PluginRef> plugins, Comparator<? super Plugin> comparator) {
        if (plugins.size() == 0)
            return;

        if (comparator == null)
            comparator = new PluginComparator(PluginComparator.TYPE.STOP);
        TreeSet<Plugin> set = new TreeSet<Plugin>(comparator);
        for (PluginRef ref : plugins) {
            if (ref.getPlugin() != null && !set.add(ref.getPlugin()))
                _LOG.warn("Found repeated plugin: " + ref.getPlugin().getName());
        }
        Iterator<Plugin> iter = set.iterator();
        while (iter.hasNext()) {
            unregPlugin(iter.next());
        }
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * jframe.core.plugin.PluginContext#unregPlugin(jframe.core.plugin.PluginRef
     * )
     */
    public void unregPlugin(PluginRef ref) {
        if (ref == null || ref.getPlugin() == null)
            return;
        Plugin plugin = ref.getPlugin();
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
            if (!ref.isUpdating()) {
                _plugins.remove(plugin.getID());
                ref.dispose();
            }
            removeDispatchSource(ref);
            ref.setPlugin(null);
        }
    }
}
