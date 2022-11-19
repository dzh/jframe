package jframe.ext.plugin;

import jframe.core.msg.Msg;
import jframe.ext.dispatch.rocketmq.RmqConst;

/**
 * Rocketmq Plugin
 *
 * @author dzh
 * @date 2019/12/25 15:22
 */
public class RocketmqPlugin extends MqPlugin {

    public void send(Msg<?> msg, String topic, String tag, String key) {
        if (msg == null) return;

        msg.setMeta(RmqConst.M_RMQ_TOPIC, topic);
        msg.setMeta(RmqConst.M_RMQ_TAG, tag);
        msg.setMeta(RmqConst.M_RMQ_Key, key);

        send(msg);
    }

    public void send(Msg<?> msg, String topic) {
        send(msg, topic, null, null);
    }
}
