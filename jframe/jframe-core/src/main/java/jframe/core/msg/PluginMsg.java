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

	private final Map<String, String> _meta = new HashMap<String, String>(6);

	private int type;

	private V msg;

	public int getType() {
		return type;
	}

	public PluginMsg<V> setType(int type) {
		this.type = type;
		return this;
	}

	public PluginMsg<V> setMeta(String key, String value) {
		_meta.put(key, value);
		return this;
	}

	public String getMeta(String key) {
		return _meta.get(key);
	}

	public PluginMsg<V> setValue(V msg) {
		this.msg = msg;
		return this;
	}

	public V getValue() {
		return msg;
	}

	public void accept(MsgVisitor<V> visitor) {
		visitor.visit(this);
	}

	public void clear() {
		_meta.clear();
		msg = null;
	}

	public String toString() {
		return "MsgType: " + getType() + ", MsgValue: " + getValue();
	}
}