/**
 * 
 */
package jframe.core.msg;

/**
 * 
 * @author dzh
 * @date Jun 18, 2013 3:40:41 PM
 */
public interface Msg<V> extends Cloneable {

	int getType(); // 类型

	Msg<V> setType(int type);

	Msg<V> setMeta(String key, String value); // 元信息

	String getMeta(String key);

	Msg<V> setValue(V msg); // 内容

	V getValue();

	void accept(MsgVisitor<V> visitor); // 遍历

	void clear();
}
