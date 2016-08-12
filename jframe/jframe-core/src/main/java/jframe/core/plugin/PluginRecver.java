/**
 * 
 */
package jframe.core.plugin;

import jframe.core.msg.Msg;
import jframe.core.msg.PluginMsg;
import jframe.core.plugin.annotation.Message;
import jframe.core.plugin.annotation.MsgInterest;
import jframe.core.plugin.annotation.MsgRecv;

/**
 * @author dzh
 * @date Oct 14, 2013 7:53:25 AM
 * @since 1.0
 */
@Message(isRecver = true)
public abstract class PluginRecver extends DefPlugin {

    public PluginRecver() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.dispatch.DispatchTarget#receive(jframe.core.msg.Msg)
     */
    @MsgRecv
    public void receive(Msg<?> msg) {
        doRecvMsg(msg);
    }

    /**
     * @param msg
     */
    abstract protected void doRecvMsg(Msg<?> msg);

    /**
     * message's meta information
     */
    public static final String MSG_PLUGIN_ID = "PluginId";

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.dispatch.DispatchTarget#interestMsg(jframe.core.msg.Msg)
     */
    @MsgInterest
    public boolean interestMsg(Msg<?> msg) {
        // don't receive itself
        if (msg == null || PluginStatus.STOP == getStatus() || getName().equals(msg.getMeta(PluginMsg.PluginName)))
            return false;
        return canRecvMsg(msg);
    }

    /**
     * @param msg
     * @return
     */
    abstract protected boolean canRecvMsg(Msg<?> msg);

}
