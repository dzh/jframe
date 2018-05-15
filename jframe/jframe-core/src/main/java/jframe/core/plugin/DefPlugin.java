/**
 * 
 */
package jframe.core.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.loader.PluginClassLoader;

/**
 * @author dzh
 * @date Oct 14, 2013 8:04:31 AM
 * @since 1.0
 */
@jframe.core.plugin.annotation.Plugin
public class DefPlugin implements Plugin {

    static Logger LOG = LoggerFactory.getLogger(DefPlugin.class);

    private PluginContext _context;

    private int _id;

    private PluginStatus _status = PluginStatus.DESTROY;

    public DefPlugin() {}

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#getStatus()
     */
    public PluginStatus getStatus() {
        return _status;
    }

    protected synchronized void setStatus(PluginStatus status) {
        _status = status;
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#init(jframe.core.plugin.PluginContext)
     */
    public void init(PluginContext context) throws PluginException {
        this._context = context;
        setStatus(PluginStatus.INIT);
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#getID()
     */
    public int getID() {
        return _id;
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#getName()
     */
    public String getName() {
        try {
            return getPluginClassLoader().getPluginCase().getPluginName();
        } catch (NullPointerException e) {
            logWarn("NullPointerException when invoke DefPlugin.getName()");
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#getPluginClassLoader()
     */
    public PluginClassLoader getPluginClassLoader() {
        return (PluginClassLoader) this.getClass().getClassLoader();
    }

    public PluginContext getContext() {
        return _context;
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#start()
     */
    public void start() throws PluginException {
        LOG.info("start plugin {}", getName());
        setStatus(PluginStatus.START);
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#stop()
     */
    public void stop() throws PluginException {
        LOG.info("stop plugin {}", getName());
        setStatus(PluginStatus.STOP);
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#destroy()
     */
    public void destroy() throws PluginException {
        LOG.info("destroy plugin {}", getName());
        setStatus(PluginStatus.DESTROY);
        getPluginClassLoader().dispose();
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.plugin.Plugin#setID(int)
     */
    public void setID(int id) {
        this._id = id;
    }

    /**
     * 发送插件状态改变通知事件
     * 
     * @param event
     */
    public void notifyEvent(PluginEvent event) {
        _context.notifyPluginEvent(event);
    }

    public String getConfig(String key) {
        return _context.getConfig().getConfig(key);
    }

    public String getConfig(String key, String defVal) {
        return _context.getConfig().getConfig(key, defVal);
    }

    public PluginRef getPluginRef() {
        return _context.getPlugin(_id);
    }

    public void logWarn(String msg) {
        LoggerFactory.getLogger(getClass()).warn(msg);
    }

    public void logError(String msg) {
        LoggerFactory.getLogger(getClass()).error(msg);
    }

    public void logInfo(String msg) {
        LoggerFactory.getLogger(getClass()).info(msg);
    }

    public void logDebug(String msg) {
        LoggerFactory.getLogger(getClass()).debug(msg);
    }

}
