package jframe.ext.plugin;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginSender;
import jframe.ext.dispatch.kafka.KafkaDispatcher;

/**
 * @author dzh
 * @version 0.0.1
 * @date Dec 27, 2018 3:05:36 PM
 */
public abstract class KafkaPlugin extends PluginSender {

    public void send(Msg<?> msg, String topic, Integer partition, Long timestamp, String key) {
        if (msg == null) return;

        msg.setMeta(KafkaDispatcher.D_KAFKA_R_TOPIC, topic);
        msg.setMeta(KafkaDispatcher.D_KAFKA_R_PARTITION, partition);
        msg.setMeta(KafkaDispatcher.D_KAFKA_R_TIMESTAMP, timestamp);
        msg.setMeta(KafkaDispatcher.D_KAFKA_R_KEY, key);

        send(msg);
    }

    public void send(Msg<?> msg, String topic, String key) {
        send(msg, topic, null, System.currentTimeMillis(), key);
    }

    public void send(Msg<?> msg, String topic) {
        send(msg, topic, null, System.currentTimeMillis(), null);
    }

}
