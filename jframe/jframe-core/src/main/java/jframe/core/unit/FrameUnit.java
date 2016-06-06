/**
 * 
 */
package jframe.core.unit;

import jframe.core.signal.Signal;

/**
 * <p>
 * FrameUnit is a bridge between Frame and Unit.
 * <li>Support signals received from the remote client which restart frame or
 * exit application and so on</li>
 * <li>Update the configuration file after modified</li>
 * </p>
 * 
 * @author dzh
 * @date Sep 23, 2013 3:17:43 PM
 * @since 1.0
 */
public class FrameUnit extends AbstractUnit {

    public FrameUnit() {
        setName(FrameUnit.class.getSimpleName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.unit.Unit#start()
     */
    public void start() throws UnitException {
        // String app_conf = getFrame().getConfig().getConfig(Config.APP_CONF);
        // String app_lib = getFrame().getConfig().getConfig(Config.APP_LIB);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.unit.Unit#stop()
     */
    public void stop() throws UnitException {
        // sendSig(Signal.newSig(Signal.SIG_UNIT_STOP).setSource(this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.unit.Unit#recvSig(jframe.core.signal.Signal)
     */
    public void recvSig(Signal sig) {
        switch (sig.getSigType()) {
        case Signal.SIG_FRAME_RESTART:
            _frame.restart();
            break;
        case Signal.SIG_FRAME_STOP:
            _frame.stop();
            break;
        }
    }

}
