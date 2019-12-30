package jframe.ext.plugin;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginSender;
import jframe.ext.dispatch.rocketmq.RmqDispatcher;

/**
 * Rocketmq Plugin
 *
 * @author dzh
 * @date 2019/12/25 15:22
 */
public abstract class RocketmqPlugin extends PluginSender {

    public void send(Msg<?> msg, String topic, String tag, String key) {
        if (msg == null) return;

        msg.setMeta(RmqDispatcher.D_RMQ_R_TOPIC, topic);
        msg.setMeta(RmqDispatcher.D_RMQ_R_TAG, tag);
        msg.setMeta(RmqDispatcher.D_RMQ_R_Key, key);

        send(msg);
    }

    public void send(Msg<?> msg, String topic) {
        send(msg, topic, null, null);
    }
}
