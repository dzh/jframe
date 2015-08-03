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

	public static PluginMsg<String> build(int type) {
		return new TextMsg().setType(type);
	}

}
