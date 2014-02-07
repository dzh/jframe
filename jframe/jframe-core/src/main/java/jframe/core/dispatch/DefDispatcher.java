/**
 * 
 */
package jframe.core.dispatch;

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

	// TODO size
	private final BlockingQueue<Msg<?>> _queue = new LinkedBlockingQueue<Msg<?>>();

	private volatile boolean stop;

	private final CountDownLatch latch = new CountDownLatch(1);

	private DefDispatcher(String id) {
		this._ID = id;
	}

	protected static final Dispatcher newDispatcher(String id) {
		return new DefDispatcher(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.weather.datam.delegate.IDelegate#delegate(com.weather.datam.message
	 * .Message)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.weather.datam.delegate.Delegate#getID()
	 */
	public String getID() {
		return _ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.weather.datam.delegate.Delegate#addDelegateSource(com.weather.datam
	 * .delegate.DelegateSource)
	 */
	public void addDispatchSource(DispatchSource source) {
		if (source == null || _dsList.contains(source))
			return;

		source.addDispatch(this);
		_dsList.add(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.weather.datam.delegate.Delegate#removeDelegateSource(com.weather.
	 * datam.delegate.DelegateSource)
	 */
	public void removeDispatchSource(DispatchSource source) {
		if (source == null)
			return;
		source.removeDispatch(this);
		_dsList.remove(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.weather.datam.delegate.Delegate#addDelegateTarget(com.weather.datam
	 * .delegate.DelegateTarget)
	 */
	public void addDispatchTarget(DispatchTarget target) {
		if (target == null || _dtList.contains(target))
			return;
		_dtList.add(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.weather.datam.delegate.Delegate#removeDelegateTarget(com.weather.
	 * datam.delegate.DelegateTarget)
	 */
	public void removeDispatchTarget(DispatchTarget target) {
		if (target == null)
			return;
		_dtList.remove(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.weather.datam.delegate.Delegate#start()
	 */
	public void start() {
		LOG.info("Delegate: " + _ID + " Starting!");
		stop = false;
		final BlockingQueue<Msg<?>> queue = _queue;
		new Thread("Dispacher") { // 分发线程
			public void run() {
				while (true) {
					if (queue.isEmpty() && stop) {
						latch.countDown();
						break;
					}

					Msg<?> msg = null;
					try {
						msg = queue.poll(2000, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						LOG.warn(e.getMessage());
					}

					dispatch(msg);
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.weather.datam.delegate.Delegate#receive(com.weather.datam.message
	 * .Msg)
	 */
	public void receive(Msg<?> msg) {
		if (msg == null)
			return;
		_queue.offer(msg);
		// LOG.debug("DefDispatcher receive msg " + msg.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.weather.datam.delegate.Delegate#close()
	 */
	public void close() {
		if (stop)
			return;
		LOG.info("Dispacher: " + _ID + " Stopping!");
		stop = true;
		try {
			latch.await();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		dispose();
	}

	/**
	 * 内部方法清理对象
	 */
	private void dispose() {
		List<DispatchSource> dslist = _dsList;
		for (DispatchSource ds : dslist) {
			ds.removeDispatch(this);
		}
		_dtList.clear();
	}

}
