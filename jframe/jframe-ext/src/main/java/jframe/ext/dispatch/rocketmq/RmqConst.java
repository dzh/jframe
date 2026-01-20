package jframe.ext.dispatch.rocketmq;

/**
 * @author dzh
 * @date 2020/1/11 19:35
 */
public interface RmqConst {

    String FILE_RMQ_PRODUCER = "file.rmq.producer";
    String FILE_RMQ_CONSUMER = "file.rmq.consumer";

    // msg meta
    String M_RMQ_CODEC = "m.rmq.codec"; // MsgCodec
    String M_RMQ_TOPIC = "m.rmq.topic";
    String M_RMQ_TAG = "m.rmq.tag";
    String M_RMQ_Key = "m.rmq.key";

    String DEFAULT_TOPIC = "jframe";
}
