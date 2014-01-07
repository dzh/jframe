/**
 * 
 */
package jframe.core.msg;

/**
 * @author dzh
 * @date Jun 19, 2013 2:48:02 PM
 */
public interface MsgVisitor<V> {

	void visit(Msg<V> msg);

}
