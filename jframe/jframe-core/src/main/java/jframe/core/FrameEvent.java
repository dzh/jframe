/**
 * 
 */
package jframe.core;

import java.util.EventObject;

/**
 * @author dzh
 * @date Sep 12, 2013 9:30:49 PM
 * @since 1.0
 */
public class FrameEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int type;

    public static final int Init = 1 << 0;
    public static final int Start = 1 << 1;
    public static final int Stop = 1 << 2;

    // public static final int Update = 0x4;
    // public static final int Error = -1;

    /**
     * @param source
     */
    public FrameEvent(int type, Object source) {
        super(source);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        return getClass().getName() + "[source=" + source.toString() + "]";
    }

}
