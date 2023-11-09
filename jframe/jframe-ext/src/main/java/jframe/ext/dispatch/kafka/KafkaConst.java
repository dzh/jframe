package jframe.ext.dispatch.kafka;

/**
 * @author dzh
 * @date 2022/11/18 11:13
 */
public interface KafkaConst {

    // default
    String DEFAULT_TOPIC = "jframe";

    // config
    String FILE_KAFKA_PRODUCER = "file.kafka.producer";
    String FILE_KAFKA_CONSUMER = "file.kafka.consumer";
    String D_KAFKA_SUBSCRIBE = "d.kafka.subscribe";
    String D_KAFKA_SUBSCRIBE_REGEX = "d.kafka.subscribe.regex";

    // msg meta
    String M_KAFKA_TOPIC = "m.kafka.topic";
    String M_KAFKA_KEY = "m.kafka.key";
    String M_KAFKA_PARTITION = "m.kafka.partition";
    String M_KAFKA_TIMESTAMP = "m.kafka.timestamp";

}
