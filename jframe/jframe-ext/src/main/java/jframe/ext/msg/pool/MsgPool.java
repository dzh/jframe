/**
 * 
 */
package jframe.ext.msg.pool;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jframe.core.msg.Msg;
import jframe.core.msg.PluginMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <li>消息创建</li>
 * <li>和PoolDispatcher配对使用</li>
 * <li>TODO 限制容量</li>
 * </p>
 * 
 * @author dzh
 * @date Apr 4, 2014 2:16:56 PM
 * @since 1.0
 */
public class MsgPool<V> {

	private static final Logger LOG = LoggerFactory.getLogger(MsgPool.class);

	private Queue<SoftReference<Msg<V>>> cacheQueue;

	private final ReferenceQueue<Msg<V>> _rq = new ReferenceQueue<Msg<V>>();

	private volatile boolean stop = false;

	private final Thread clrRQ;

	private MsgPool() {
		cacheQueue = new ConcurrentLinkedQueue<SoftReference<Msg<V>>>();
		clrRQ = new Thread("Clear RQ") {
			public void run() {
				while (!stop) {
					try {
						cacheQueue.remove(_rq.remove());
					} catch (Exception e) {
						LOG.warn(e.getMessage());
					}
				}
			}
		};
		clrRQ.setDaemon(true);
		clrRQ.start();
	}

	public Msg<V> getMsg() {
		SoftReference<Msg<V>> msg = cacheQueue.poll();
		if (msg == null || msg.get() == null) {
			msg = new SoftReference<Msg<V>>(new PluginMsg<V>(), _rq);
			try {
				cacheQueue.add(msg);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
			return msg.get();
		}
		return msg.get();
	}

	public void addMsg(Msg<V> msg) {
		if (msg == null)
			return;
		msg.clear();
		cacheQueue.add(new SoftReference<Msg<V>>(msg, _rq));
	}
	
	void close() {
		stop = true;
		clrRQ.interrupt();

		cacheQueue.clear();
		cacheQueue = null;
		LOG.info("MsgPool is closed!");
	}

}
