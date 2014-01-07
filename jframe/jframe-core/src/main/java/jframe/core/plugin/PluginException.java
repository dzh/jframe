/**
 * 
 */
package jframe.core.plugin;

/**
 * @author dzh
 * @date Jun 13, 2013 3:31:36 PM
 */
public class PluginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PluginException() {
		super();
	}

	public PluginException(String message) {
		super(message);
	}

	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginException(Throwable cause) {
		super(cause);
	}

}
