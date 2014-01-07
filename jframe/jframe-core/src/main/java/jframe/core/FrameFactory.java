/**
 * 
 */
package jframe.core;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date Jun 7, 2013 1:44:41 PM
 */
public interface FrameFactory {

	Frame createFrame(Config config);

	void handleException(Throwable exception);

}
