package jframe.ext.plugin;

import jframe.core.msg.Msg;
import jframe.ext.dispatch.kafka.KafkaConst;

/**
 * @author dzh
 * @version 0.0.1
 * @date Dec 27, 2018 3:05:36 PM
 */
public class KafkaPlugin extends MqPlugin {

    public void send(Msg<?> msg, String topic, Integer partition, Long timestamp, String key) {
        if (msg == null) return;

        msg.setMeta(KafkaConst.M_KAFKA_TOPIC, topic);
        msg.setMeta(KafkaConst.M_KAFKA_PARTITION, partition);
        msg.setMeta(KafkaConst.M_KAFKA_TIMESTAMP, timestamp);
        msg.setMeta(KafkaConst.M_KAFKA_KEY, key);

        send(msg);
    }

    public void send(Msg<?> msg, String topic, String key) {
        send(msg, topic, null, System.currentTimeMillis(), key);
    }

    public void send(Msg<?> msg, String topic) {
        send(msg, topic, null, System.currentTimeMillis(), null);
    }

}
