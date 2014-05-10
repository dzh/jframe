/**
 * 
 */
package jframe.ext.msg.pool;

import jframe.core.msg.PluginMsg;

/**
 * @author dzh
 * @date Apr 8, 2014 5:07:46 PM
 * @since 1.0
 */
public class ResuableMsg<V> extends PluginMsg<V> implements Reusable {

	private static MsgPool<?> _pool;

	ResuableMsg() {
	}

	public synchronized static final <T> void setObjectPool(MsgPool<T> pool) {
		if (_pool == null)
			_pool = pool;
	}

	public ResuableMsg createMsg() {
		return _pool.getMsg();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.ext.msg.pool.Reusable#reuse()
	 */
	public void reuse() {

	}

	public static final class TextMsg extends ResuableMsg<String> {
	}
}
