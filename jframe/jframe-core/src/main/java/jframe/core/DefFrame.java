/**
 * 
 */
package jframe.core;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.signal.Signal;
import jframe.core.unit.Unit;
import jframe.core.unit.UnitException;
import jframe.core.unit.UnitManager;

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

    private volatile FRAME_STATUS _status = FRAME_STATUS.INIT;

    private final Object _lock = new Object();

    /*
     * (non-Javadoc)
     * @see jframe.core.Frame#init(jframe.core.conf.Config)
     */
    public boolean init(Config conf) {
        synchronized (_lock) {
            if (_status == FRAME_STATUS.START) {
                LOG.error("m->init Invalid status->{}", _status);
                return false;
            }
            _status = FRAME_STATUS.INIT;
        }

        LOG.debug("DefFrame is initing");
        conf.setFrame(this);

        this._cnf = conf;
        this._um = UnitManager.createManager(conf);
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                LOG.error(t.toString(), e);
            }
        });
        return true;
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.Frame#start()
     */
    public void start() {
        synchronized (_lock) {
            if (_status != FRAME_STATUS.INIT) {
                LOG.error("m->start Invalid status->{}", _status);
                return;
            }
            _status = FRAME_STATUS.START;
        }
        LOG.debug("DefFrame is starting");
        _gate.close();

        final UnitManager um = this._um;
        new Thread("DefFrameStart") {
            public void run() {
                try {
                    um.start();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e.fillInStackTrace());
                    // DefFrame.this.stop(); // Exit Frame
                    // TODO Exit daemon process
                }
            }
        }.start();
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.Frame#stop()
     */
    public void stop() {
        synchronized (_lock) {
            if (_status == FRAME_STATUS.STOP) { return; }
            _status = FRAME_STATUS.STOP;
        }
        LOG.debug("DefFrame is stopping");
        try {
            _um.dispose();
            _gate.open();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        LOG.info("DefFrame stopped successfully!");
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
     * @see jframe.core.Frame#broadcast(jframe.core.signal.Signal)
     */
    public void broadcast(Signal sig) {
        for (Unit u : _um.getAllUnits()) {
            u.recvSig(sig);
        }
    }

    /*
     * (non-Javadoc)
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
     * @see jframe.core.Frame#getConfig()
     */
    public Config getConfig() {
        return _cnf;
    }

}
