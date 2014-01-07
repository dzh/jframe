/**
 * 
 */
package jframe.core.dispatch;

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
	Dispatcher createDispatcher(String dispatcherID);

	Dispatcher findDispatcher(String dispatcherID);

	void removeDispacher(String dispatcherID);

}
