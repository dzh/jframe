/**
 * 
 */
package jframe.core.dispatch;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import jframe.core.msg.Msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ThreadSafe
 * @author dzh
 * @date Jun 18, 2013 4:21:18 PM
 */
public class DefDispatcher implements Dispatcher {
	private static final Logger LOG = LoggerFactory
			.getLogger(DefDispatcher.class);

	private String _ID;

	private final List<DispatchSource> _dsList = new CopyOnWriteArrayList<DispatchSource>();

	private final List<DispatchTarget> _dtList = new CopyOnWriteArrayList<DispatchTarget>();

	private BlockingQueue<Msg<?>> _queue;

	private volatile boolean stop;

	private final CountDownLatch latch = new CountDownLatch(1);

	public DefDispatcher(String id) {
		this._ID = id;
	}

	public static final Dispatcher newDispatcher(String id) {
		return new DefDispatcher(id);
	}

	public boolean dispatch(Msg<?> msg) {
		if (msg != null) {
			List<DispatchTarget> dtList = _dtList;
			for (DispatchTarget dt : dtList) {
				if (dt.interestMsg(msg)) {
					dt.receive(msg);
				}
			}
		}
		return false;
	}

	public String getID() {
		return _ID;
	}

	public void addDispatchSource(DispatchSource source) {
		if (source == null || _dsList.contains(source))
			return;

		source.addDispatch(this);
		_dsList.add(source);
	}

	public void removeDispatchSource(DispatchSource source) {
		if (source == null)
			return;
		source.removeDispatch(this);
		_dsList.remove(source);
	}

	public void addDispatchTarget(DispatchTarget target) {
		if (target == null || _dtList.contains(target))
			return;
		_dtList.add(target);
	}

	public void removeDispatchTarget(DispatchTarget target) {
		if (target == null)
			return;
		_dtList.remove(target);
	}

	private Thread disptchThread;

	public void start() {
		LOG.info("Dispatcher: " + _ID + " Starting!");
		stop = false;
		_queue = createDispatchQueue();
		initDispatchQueue(_queue);
		disptchThread = new Thread("DispatchThread") { // 分发线程
			public void run() {
				final BlockingQueue<Msg<?>> queue = _queue;
				while (true) {
					try {
						if (queue.isEmpty() && stop)
							break;
						dispatch(queue.take());
					} catch (Exception e) {
						LOG.warn(e.getMessage());
					}
				}
				latch.countDown();
			}
		};
		disptchThread.start();
	}

	/**
	 * 启动时，初始化分发队列
	 * 
	 * @param queue
	 */
	void initDispatchQueue(BlockingQueue<Msg<?>> queue) {

	}

	/**
	 * 关闭时，保存队列数据
	 * 
	 * @param _queue
	 */
	void saveDispatchQueue(BlockingQueue<Msg<?>> queue) {

	}

	/**
	 * 创建分发队列
	 * 
	 * @return
	 */
	BlockingQueue<Msg<?>> createDispatchQueue() {
		return new LinkedBlockingQueue<Msg<?>>();
	}

	public void receive(Msg<?> msg) {
		if (msg == null || stop)
			return;
		try {
			_queue.offer(msg, 60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
			// TODO 数据丢失问题
		}
		// LOG.debug("DefDispatcher receive msg " + msg.toString());
	}

	/**
	 * 
	 */
	public void close() {
		if (stop)
			return;
		LOG.info("Dispacher: " + _ID + " Stopping!");
		closeDispatch();
		dispose();
		saveDispatchQueue(_queue);
	}

	/**
	 * close DispatchThread
	 */
	void closeDispatch() {
		stop = true;
		disptchThread.interrupt();
		try {
			latch.await();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * cleanup DispatchSource and DispatchTarget
	 */
	private void dispose() {
		List<DispatchSource> dslist = _dsList;
		for (DispatchSource ds : dslist) {
			ds.removeDispatch(this);
		}
		_dtList.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.Dispatcher#getDispatchSource()
	 */
	public Collection<DispatchSource> getDispatchSource() {
		return Collections.unmodifiableList(_dsList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.Dispatcher#getDispatchTarget()
	 */
	public Collection<DispatchTarget> getDispatchTarget() {
		return Collections.unmodifiableList(_dtList);
	}

}
