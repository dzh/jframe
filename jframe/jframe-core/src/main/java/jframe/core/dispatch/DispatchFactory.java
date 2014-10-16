/**
 * 
 */
package jframe.core.dispatch;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date Jun 20, 2013 9:45:03 AM
 */
public interface DispatchFactory {

	/**
	 * create a new dispatcher instance
	 * 
	 * @param dispatcherID
	 * @return
	 */
	Dispatcher createDispatcher(String dispatcherID, Config conf);

	Dispatcher findDispatcher(String dispatcherID);

	void removeDispatcher(String dispatcherID);

}
