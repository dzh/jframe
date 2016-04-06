/**
 * 
 */
package jframe.core.unit;

/**
 * @author dzh
 * @date Sep 24, 2013 2:12:50 PM
 * @since 1.0
 */
public class UnitException extends Exception {

    /**
     * @param string
     */
    public UnitException(String message) {
        super(message);
    }

    public UnitException(Throwable cause) {
        super(cause);
    }

    public UnitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
