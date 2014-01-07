/**
 * 
 */
package jframe.core;

import jframe.core.conf.Config;
import jframe.core.signal.Signal;
import jframe.core.unit.Unit;

/**
 * <p>
 * Frame Features:
 * <li>Manage frame's life cycle</li>
 * <li>Manage units</li>
 * <li></li>
 * </p>
 * 
 * @author dzh
 * @date Sep 12, 2013 9:19:38 PM
 * @since 1.0
 */
public interface Frame {

	void register(Unit u);

	void unregister(Unit u);

	void init(Config conf);

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
