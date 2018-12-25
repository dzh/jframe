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

    Msg<V> setMeta(String key, Object value); // 元信息

    Object getMeta(String key);

    Object removeMeta(String key);

    Msg<V> setValue(V msg); // 内容

    V getValue();

    void accept(MsgVisitor<V> visitor); // 遍历

    void clear();

    /**
     * delete all meta content
     */
    void clearMeta();
}
