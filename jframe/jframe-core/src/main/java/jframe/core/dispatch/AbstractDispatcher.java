/**
 * 
 */
package jframe.core.dispatch;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jframe.core.conf.Config;
import jframe.core.msg.Msg;

/**
 * @author dzh
 * @date Oct 16, 2014 10:50:28 AM
 * @since 1.1
 */
public abstract class AbstractDispatcher implements Dispatcher {

	private String _ID;

	private Config _conf;

	protected final List<DispatchSource> _dsList = new CopyOnWriteArrayList<DispatchSource>();

	protected final List<DispatchTarget> _dtList = new CopyOnWriteArrayList<DispatchTarget>();

	public AbstractDispatcher(String id, Config config) {
		this._ID = id;
		this._conf = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.Dispatcher#getID()
	 */
	public String getID() {
		return _ID;
	}

	public Config getConfig() {
		return _conf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.Dispatcher#dispatch(jframe.core.msg.Msg)
	 */
	public void dispatch(Msg<?> msg) {
		if (msg != null) {
			List<DispatchTarget> dtList = _dtList;
			for (DispatchTarget dt : dtList) {
				if (dt.interestMsg(msg)) {
					dt.receive(msg);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.dispatch.Dispatcher#addDispatchSource(jframe.core.dispatch
	 * .DispatchSource)
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
	 * jframe.core.dispatch.Dispatcher#removeDispatchSource(jframe.core.dispatch
	 * .DispatchSource)
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
	 * jframe.core.dispatch.Dispatcher#addDispatchTarget(jframe.core.dispatch
	 * .DispatchTarget)
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
	 * jframe.core.dispatch.Dispatcher#removeDispatchTarget(jframe.core.dispatch
	 * .DispatchTarget)
	 */
	public void removeDispatchTarget(DispatchTarget target) {
		if (target == null)
			return;
		_dtList.remove(target);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.Dispatcher#close()
	 */
	public void close() {
		dispose();
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

}
