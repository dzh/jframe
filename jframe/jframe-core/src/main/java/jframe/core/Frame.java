/**
 * 
 */
package jframe.core;

import jframe.core.conf.Config;
import jframe.core.signal.Signal;
import jframe.core.unit.Unit;

/**
 * <p>
 * Features:
 * <li>Manage frame's life cycle</li>
 * <li>Manage units</li>
 * <li></li>
 * </p>
 * 
 * <p>
 * Life-cycle:
 * <li>INIT -> START -> STOP</li>
 * <li>STOP -> INIT -> START</li>
 * <li>INIT -> STOP</li>
 * </p>
 * 
 * @author dzh
 * @date Sep 12, 2013 9:19:38 PM
 * @since 1.0
 */
public interface Frame {

    static enum FRAME_STATUS {
        INIT, START, STOP
    };

    void register(Unit u);

    void unregister(Unit u);

    boolean init(Config conf);

    void start();

    void stop();

    void restart();

    void broadcast(Signal sig);

    Config getConfig();

    /**
     * @param timeout
     * @return
     */
    FrameEvent waitForStop(long timeout);

}
