/**
 * 
 */
package jframe.ext.msg.pool;

import jframe.core.conf.Config;
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

	public MsgPoolDispatcher(String id, Config config) {
		super(id, config);
	}

	private MsgPool<V> msgPool = null;

	public void dispatch(Msg<?> msg) {

	}

	@Override
	public void close() {
		super.close();
		if (msgPool != null)
			msgPool.close();
	}

}
