package jframe.ext.plugin;

import jframe.core.msg.Msg;
import jframe.ext.dispatch.mns.MnsConst;

/**
 * @author dzh
 * @date 2022/11/17 18:06
 */
public class MnsPlugin extends MqPlugin {

    public void send(Msg<?> msg, String queue) {
        if (msg == null) return;

        msg.setMeta(MnsConst.M_MNS_QUEUE, queue);

        send(msg);
    }

}
