/**
 * 
 */
package jframe.core.msg;

/**
 * @author dzh
 * @date Oct 9, 2013 5:05:16 PM
 * @since 1.0
 */
public class TextMsg extends PluginMsg<String> {

    public static TextMsg build(int type) {
        TextMsg msg = new TextMsg();
        msg.setType(type);
        return msg;
    }

}
