/**
 * 
 */
package jframe.core;

import java.lang.Thread.UncaughtExceptionHandler;

import jframe.core.conf.Config;
import jframe.core.signal.Signal;
import jframe.core.unit.Unit;
import jframe.core.unit.UnitException;
import jframe.core.unit.UnitManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ThreadSafe
 * @author dzh
 * @date Sep 23, 2013 2:44:01 PM
 * @since 1.0
 */
public class DefFrame implements Frame {
	private static final Logger LOG = LoggerFactory.getLogger(DefFrame.class);

	private Config _cnf;
	private UnitManager _um;

	private final ThreadGate _gate = new ThreadGate();

	public static enum FRAME_STATUS {
		INIT, START, STOP
	};

	private FRAME_STATUS _status = FRAME_STATUS.INIT;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.Frame#init(jframe.core.conf.Config)
	 */
	public void init(Config conf) {
		_status = FRAME_STATUS.INIT;
		conf.setFrame(this);

		this._cnf = conf;
		this._um = UnitManager.createManager(conf);
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				LOG.error(t.toString(), e);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.Frame#start()
	 */
	public void start() {
		if (_status != FRAME_STATUS.INIT)
			return;
		_status = FRAME_STATUS.START;
		_gate.close();

		final UnitManager um = this._um;
		new Thread("DefFrameStartThread") {
			public void run() {
				try {
					um.start();
				} catch (UnitException e) {
					LOG.error("UnitManager starting error, "
							+ e.getLocalizedMessage());
					// DefFrame.this.stop(); // Exit Frame
					// TODO Exit daemon process
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.Frame#stop()
	 */
	public void stop() {
		LOG.debug("DefFrame is stopping");
		if (_status == FRAME_STATUS.STOP)
			return;
		_status = FRAME_STATUS.STOP;
		new Thread("DefFrameStopThread") {
			public void run() {
				_um.dispose();
				_gate.open();
				LOG.info("DefFrame stopped successfully!");
			}
		}.start();
	}

	public FrameEvent waitForStop(long timeout) {
		try {
			_gate.await(timeout);
		} catch (InterruptedException e) {
			LOG.error(e.getLocalizedMessage());
		}

		FrameEvent event = null;
		switch (_status) {
		case INIT:
			event = new FrameEvent(FrameEvent.Init, this);
			break;
		case START:
			event = new FrameEvent(FrameEvent.Start, this);
			break;
		default:
			event = new FrameEvent(FrameEvent.Stop, this);
			break;
		}
		return event;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.Frame#restart()
	 */
	public void restart() {
		if (_status == FRAME_STATUS.START) {
			stop();
		}
		if (_status == FRAME_STATUS.STOP) {
			init(_cnf);
		}
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.Frame#broadcast(jframe.core.signal.Signal)
	 */
	public void broadcast(Signal sig) {
		for (Unit u : _um.getAllUnits()) {
			u.recvSig(sig);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.Frame#register(jframe.core.unit.Unit)
	 */
	public void register(Unit u) {
		try {
			_um.regUnit(u);
		} catch (UnitException e) {
			LOG.warn(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.Frame#unregister(jframe.core.unit.Unit)
	 */
	public void unregister(Unit u) {
		try {
			_um.unregUnit(u);
		} catch (UnitException e) {
			LOG.warn(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.Frame#getConfig()
	 */
	public Config getConfig() {
		return _cnf;
	}

}
