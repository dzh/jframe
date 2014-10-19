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

	private final Map<String, String> meta = new HashMap<String, String>(10);

	private int type;

	private V value;

	public int getType() {
		return type;
	}

	public PluginMsg<V> setType(int type) {
		this.type = type;
		return this;
	}

	public PluginMsg<V> setMeta(String key, String value) {
		meta.put(key, value);
		return this;
	}

	public String getMeta(String key) {
		return meta.get(key);
	}

	public PluginMsg<V> setValue(V msg) {
		this.value = msg;
		return this;
	}

	public V getValue() {
		return value;
	}

	public void accept(MsgVisitor<V> visitor) {
		visitor.visit(this);
	}

	public void clear() {
		clearMeta();
		value = null;
	}

	public String toString() {
		return "MsgType: " + getType() + ", MsgValue: " + getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#clearMeta()
	 */
	public void clearMeta() {
		meta.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#removeMeta(java.lang.String)
	 */
	public String removeMeta(String key) {
		return meta.remove(key);
	}
}