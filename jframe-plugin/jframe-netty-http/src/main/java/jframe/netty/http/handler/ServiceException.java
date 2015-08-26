/**
 * 
 */
package jframe.netty.http.handler;

/**
 * @author dzh
 * @date Aug 16, 2014 9:26:23 AM
 * @since 1.0
 */
public class ServiceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}
}
