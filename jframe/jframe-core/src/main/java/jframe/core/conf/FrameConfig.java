/**
 * 
 */
package jframe.core.conf;

import java.util.HashMap;
import java.util.Map;

import jframe.core.Frame;

/**
 * @ThreadSafe
 * @author dzh
 * @date Sep 23, 2013 1:44:06 PM
 * @since 1.0
 */
public class FrameConfig implements Config {

    private Map<String, String> _config = new HashMap<String, String>(20);
    // private Collection<ConfigListener> _listeners = new
    // LinkedList<ConfigListener>();

    private Frame _frame;

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#addConfig(java.lang.String,
     * java.lang.String)
     */
    public synchronized String setConfig(String key, String value) {
        return _config.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#getConfig(java.lang.String)
     */
    public synchronized String getConfig(String key) {
        return _config.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#getConfig(java.lang.String,
     * java.lang.String)
     */
    public synchronized String getConfig(String key, String defVal) {
        if (_config.containsKey(key))
            return _config.get(key);
        return defVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#modifyConfig(java.lang.String,
     * java.lang.String)
     */
    public synchronized String modifyConfig(String key, String value) {
        return _config.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#clearConfig()
     */
    public synchronized void clearConfig() {
        _config.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#setFrame(jframe.core.Frame)
     */
    public void setFrame(Frame frame) {
        _frame = frame;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#getFrame()
     */
    public Frame getFrame() {
        return _frame;
    }

    public synchronized Map<String, String> getConfig() {
        return new HashMap<String, String>(_config);
    }

    public synchronized boolean contain(String k) {
        return _config.containsKey(k);
    }

}
