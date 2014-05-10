/**
 * 
 */
package jframe.ext.msg.pool;

import jframe.core.dispatch.DefDispatcher;
import jframe.core.msg.Msg;

/**
 * <p>
 * 利用MsgPool实现安全转发
 * <li>基于MsgPool的安全消息分发</li>
 * <li>消息的可持久化</li>
 * </p>
 * 
 * @author dzh
 * @date Apr 4, 2014 2:19:26 PM
 * @since 1.0
 */
public class MsgPoolDispatcher<V> extends DefDispatcher {

	private MsgPool<V> msgPool = null;

	/**
	 * @param id
	 */
	protected MsgPoolDispatcher(String id) {
		super(id);
		//msgPool = new MsgPool<V>();
	}

	public boolean dispatch(Msg<?> msg) {

		return false;
	}

	@Override
	public void close() {
		super.close();
		if (msgPool != null)
			msgPool.close();
	}

}
