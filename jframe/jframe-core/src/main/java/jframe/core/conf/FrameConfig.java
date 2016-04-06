/**
 * 
 */
package jframe.core.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jframe.core.Frame;

/**
 * @ThreadSafe
 * @author dzh
 * @date Sep 23, 2013 1:44:06 PM
 * @since 1.0
 */
public class FrameConfig implements Config {

    private ConcurrentMap<String, String> _config = new ConcurrentHashMap<String, String>(20);
    // private Collection<ConfigListener> _listeners = new
    // LinkedList<ConfigListener>();

    private Frame _frame;

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#addConfig(java.lang.String,
     * java.lang.String)
     */
    public String setConfig(String key, String value) {
        return _config.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#getConfig(java.lang.String)
     */
    public String getConfig(String key) {
        return _config.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#getConfig(java.lang.String,
     * java.lang.String)
     */
    public String getConfig(String key, String defVal) {
        String v = _config.get(key);
        return v == null ? defVal : v;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#modifyConfig(java.lang.String,
     * java.lang.String)
     */
    public String modifyConfig(String key, String value) {
        return _config.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.conf.Config#clearConfig()
     */
    public void clearConfig() {
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

    public Map<String, String> getConfig() {
        return new HashMap<String, String>(_config);
    }

    public boolean contain(String k) {
        return _config.containsKey(k);
    }

}
