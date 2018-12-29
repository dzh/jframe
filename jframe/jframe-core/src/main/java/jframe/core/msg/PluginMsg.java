package jframe.core.msg;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类的目的用于插件间消息传递，
 * 
 * @author dzh
 * @date Jul 9, 2013 12:21:59 PM
 * @since 1.0
 * @NotThreadSafe
 */
public class PluginMsg<V> implements Msg<V> {

    public static final String PluginName = "Plugin";

    private final Map<String, Object> meta = new HashMap<String, Object>(8);

    private int type;

    private V value;

    @Override
    public int getType() {
        return type;
    }

    @Override
    public PluginMsg<V> setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public PluginMsg<V> setMeta(String key, Object value) {
        meta.put(key, value);
        return this;
    }

    @Override
    public Object getMeta(String key) {
        return meta.get(key);
    }

    @Override
    public PluginMsg<V> setValue(V msg) {
        this.value = msg;
        return this;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void accept(MsgVisitor<V> visitor) {
        visitor.visit(this);
    }

    @Override
    public void clear() {
        clearMeta();
        value = null;
    }

    @Override
    public String toString() {
        return "type: " + getType() + ", value: " + getValue();
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.msg.Msg#clearMeta()
     */
    @Override
    public void clearMeta() {
        meta.clear();
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.msg.Msg#removeMeta(java.lang.String)
     */
    @Override
    public Object removeMeta(String key) {
        return meta.remove(key);
    }

}