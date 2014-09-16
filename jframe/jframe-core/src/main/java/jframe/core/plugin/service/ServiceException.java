/**
 * 
 */
package jframe.core.plugin.service;

/**
 * @author dzh
 * @date Sep 16, 2014 12:12:26 PM
 * @since 1.1
 */
public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

}
