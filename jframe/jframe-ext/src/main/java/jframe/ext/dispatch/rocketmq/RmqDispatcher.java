package jframe.ext.dispatch.rocketmq;

import jframe.core.conf.Config;
import jframe.core.dispatch.AbstractDispatcher;
import jframe.core.msg.Msg;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @author dzh
 * @date 2019/12/25 13:02
 */
public class RmqDispatcher extends AbstractDispatcher {

    static Logger LOG = LoggerFactory.getLogger(RmqDispatcher.class);

//    private volatile boolean closed;

    private DefaultMQProducer producer;
    private DefaultMQPushConsumer consumer;

    public static final String FILE_RMQ_PRODUCER = "file.rmq.producer";
    public static final String FILE_RMQ_CONSUMER = "file.rmq.consumer";

    public static final String D_RMQ_CODEC = "d.rmq.codec"; // MsgCodec

    public static final String DEFAULT_TOPIC = "jframe";
    public static final String D_RMQ_R_TOPIC = "d.rmq.r.topic";
    public static final String D_RMQ_R_TAG = "d.rmq.r.tag";
    public static final String D_RMQ_R_Key = "d.rmq.r.key";

//    private Thread dispatchT; // consume dispatch thread

    private MsgCodec msgCodec;

    public RmqDispatcher(String id, Config config) {
        super(id, config);
    }

    protected boolean enableProducer() {
        return true;
    }

    protected boolean enableConsumer() {
        return true;
    }

    @Override
    public void start() {
        try {
            initMsgCodec();
            if (enableConsumer()) startConsumer();
            if (enableProducer()) startProducer();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            close();
        }
    }

    private void initMsgCodec() {
        String clazz = getConfig().getConfig(D_RMQ_CODEC, TextMsgCodec.class.getName());
        try {
            msgCodec = (MsgCodec) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void startProducer() throws MQClientException {
        String file = getConfig().getConfig(FILE_RMQ_PRODUCER);
        if (Objects.isNull(file)) {
            file = getConfig().getConfig(Config.APP_CONF) + "/rmq-producer.properties";
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            loadDefaultProducer(props);
        }

        LOG.info("start producer with {}", props);
        producer = new DefaultMQProducer();
        if (props.containsKey("producer.group")) {
            producer.setProducerGroup(props.getProperty("producer.group"));
        }
        if (props.containsKey("namespace")) {
            producer.setNamespace(props.getProperty("namespace"));
        }
        producer.setNamesrvAddr(props.getProperty("namesrv.addr", "localhost:9876"));
        producer.setRetryTimesWhenSendFailed(Integer.parseInt(props.getProperty("retry.times", "3")));
        producer.setVipChannelEnabled(Boolean.parseBoolean(props.getProperty("vipChannelEnabled", "false")));
//        LOG.info("start producer {} {}", producer.getProducerGroup(), producer.getNamesrvAddr());
        producer.start();
//        List<?> mqList = producer.fetchPublishMessageQueues(topic());
//        if (mqList == null || mqList.isEmpty()) {
//            producer.createTopic(topicKey(), topic(), topicQueueNum());
//        }
    }

    private void loadDefaultProducer(Properties props) {
        props.put("producer.group", getConfig().getConfig(Config.APP_NAME));
        props.put("namesrv.addr", "localhost:9876");
        props.put("retry.times", "3");
        props.put("vipChannelEnabled", "false");
    }

    private void startConsumer() {
        String file = getConfig().getConfig(FILE_RMQ_CONSUMER);
        if (Objects.isNull(file)) {
            file = getConfig().getConfig(Config.APP_CONF) + "/rmq-consumer.properties";
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            loadDefaultConsumer(props);
        }
        LOG.info("start consumer with {}", props);
        try {
            consumer = createConsumer(props);
            consumer.start();
//            consumer.seekToEnd(Collections.emptyList());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            close();
        }

        // pull mode
//        this.dispatchT = new Thread(() -> {
//            LOG.info("{} start", Thread.currentThread().getName());
//            while (true) {
//                if (closed) break;
//                try {
//                } catch (Exception e) {
//                    LOG.warn(e.getMessage(), e);
//                    continue;
//                }
//                dispatch(record.value());
//            }
//            LOG.info("{} closed", Thread.currentThread().getName());
//        }, "rocketmqConsumeThread");
//        dispatchT.start();
    }

    private void loadDefaultConsumer(Properties props) {
        props.setProperty("consumer.group", getConfig().getConfig(Config.APP_NAME));
        props.setProperty("namesrv.addr", "localhost:9876");

    }

    // todo multi topics
    private DefaultMQPushConsumer createConsumer(Properties props) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(props.getProperty("consumer.group"));
        consumer.setNamesrvAddr(props.getProperty("namesrv.addr", "localhost:9876"));
        String val = props.getProperty("consume.message.batch.maxsize", "1"); //[1,1024]
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
//        consumer.setConsumeTimestamp(consumeTimestamp());
        consumer.setConsumeMessageBatchMaxSize(Integer.parseInt(val));
        val = props.getProperty("consume.timeout", "5"); //minutes
        consumer.setConsumeTimeout(Long.parseLong(val));
        consumer.subscribe(props.getProperty("consume.topic", "jframe"),
                props.getProperty("consume.subExpression", "*"));
        consumer.setVipChannelEnabled(false);
        val = props.getProperty("consume.thread.max");
        if (val != null)
            consumer.setConsumeThreadMax(Integer.parseInt(val));
        val = props.getProperty("consume.thread.min");
        if (val != null)
            consumer.setConsumeThreadMin(Integer.parseInt(val));
        val = props.getProperty("consume.instance.name");
        if (val != null)
            consumer.setInstanceName(val);
        val = props.getProperty("consume.pull.threshold");
        if (val != null)
            consumer.setPullThresholdForQueue(Integer.parseInt(val));
        val = props.getProperty("consume.concurrently.max.span");
        if (val != null)
            consumer.setConsumeConcurrentlyMaxSpan(Integer.parseInt(val));// 2000
        val = props.getProperty("consume.max.reconsume.times", "16");
        consumer.setMaxReconsumeTimes(Integer.parseInt(val)); // 16
        val = props.getProperty("consume.persist.consumer.offset.interval", "3000");
        consumer.setPersistConsumerOffsetInterval(Integer.parseInt(val)); // ms

        boolean ingoreTimeoutMsg = Boolean.parseBoolean(props.getProperty("consume.ignore.timeout.msg", "false"));
        int ingoreTimeoutMsgMs = Integer.parseInt(props.getProperty("consume.ignore.timeout.msg.ms", "10000"));
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                try {
                    if (ingoreTimeoutMsg && (System.currentTimeMillis() - msg.getBornTimestamp()) >= ingoreTimeoutMsgMs) {
                        LOG.warn("discard rmq timeout msg-{}", msg);
                        continue;
                    }

                    LOG.info("recv rmq msg-{}", msg);
                    dispatch(msgCodec.decode(msg.getBody()));
                } catch (Exception e) {
                    LOG.info(e.getMessage(), e);
                    if (msg.getReconsumeTimes() == consumer.getMaxReconsumeTimes()) {
                        LOG.warn("over MaxReconsumeTimes, discard msg-{}", msg);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        return consumer;
    }

    @Override
    public void receive(Msg<?> msg) {
        if (producer != null) {
            String topic = (String) msg.getMeta(D_RMQ_R_TOPIC);
            if (Objects.isNull(topic)) {
                topic = DEFAULT_TOPIC;
            }

            try {
                Message rmqMsg = new Message(topic,
                        (String) msg.getMeta(D_RMQ_R_TAG), (String) msg.getMeta(D_RMQ_R_Key),
                        msgCodec.encode(msg));
                SendResult r = producer.send(rmqMsg);
                LOG.debug("receive {}, sendResult {}", msg, r);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);//todo
            }
        }
    }

//    private int WAIT_CLOSED_SECOND = 60;

    @Override
    public void close() {
        // close producer
        if (enableProducer()) producer.shutdown();

        // close dispatcher and consumer
        if (enableConsumer()) {
            consumer.shutdown();
        }
        super.close();
    }
}
