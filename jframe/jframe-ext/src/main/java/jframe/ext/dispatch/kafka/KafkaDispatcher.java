package jframe.ext.dispatch.kafka;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.dispatch.AbstractDispatcher;
import jframe.core.msg.Msg;

/**
 * <p>
 * Config:
 * <li>context.dispatcher=jframe.kafka.dispatch.KafkaDispatcher</li>
 * <li>file.d.kafka.producer=${app.home}/conf/d-kafka-producer.properties</li>
 * <li>file.d.kafka.consumer=${app.home}/conf/d-kafka-consumer.properties</li>
 * <li>d.kafka.subscribe=topicA topicB topicC</i>
 * </p>
 * 
 * <p>
 * msg meta:
 * <li>d.kafka.r.topic 对应{@link ProducerRecord}的topic</li>
 * <li>d.kafka.r.key</li>
 * <li>d.kafka.r.partition</li>
 * <li>d.kafka.r.timestamp</li>
 * </p>
 * 
 * @author dzh
 * @date Dec 26, 2018 5:16:26 PM
 * @version 0.0.1
 */
public class KafkaDispatcher extends AbstractDispatcher {

    static Logger LOG = LoggerFactory.getLogger(KafkaDispatcher.class);

    // default
    public static final String DEFAULT_TOPIC = "jframe";

    // config
    public static final String FILE_KAFKA_PRODUCER = "file.kafka.producer";
    public static final String FILE_KAFKA_CONSUMER = "file.kafka.consumer";
    public static final String D_KAFKA_SUBSCRIBE = "d.kafka.subscribe";
    public static final String D_KAFKA_SUBSCRIBE_REGEX = "d.kafka.subscribe.regex";

    // msg meta
    public static final String D_KAFKA_R_TOPIC = "d.kafka.r.topic";
    public static final String D_KAFKA_R_KEY = "d.kafka.r.key";
    public static final String D_KAFKA_R_PARTITION = "d.kafka.r.partition";
    public static final String D_KAFKA_R_TIMESTAMP = "d.kafka.r.timestamp";

    private Producer<String, Msg<?>> producer;
    private Consumer<String, Msg<?>> consumer;

    private volatile boolean closed;

    private int WAIT_CLOSED_SECOND = 60;

    private Thread dispatchT; // consume dispatch thread

    public KafkaDispatcher(String id, Config config) {
        super(id, config);
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.dispatch.Dispatcher#start()
     */
    @Override
    public void start() {
        closed = false;
        if (enableConsumer()) startConsumer();
        if (enableProducer()) startProducer();
    }

    protected boolean enableProducer() {
        return true;
    }

    protected boolean enableConsumer() {
        return true;
    }

    private void startProducer() {
        String file = getConfig().getConfig(FILE_KAFKA_PRODUCER);
        if (Objects.isNull(file)) {
            file = getConfig().getConfig(Config.APP_CONF) + "/kafka-producer.properties";
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            loadDefaultProducer(props);
        }
        if (!props.containsKey("key.serializer")) {
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        }
        if (!props.containsKey("value.serializer")) {
            props.put("value.serializer", TextMsgSerializer.class.getName());
        }
        LOG.info("startProducer {}", props);
        producer = new KafkaProducer<>(props);
    }

    /**
     * http://kafka.apache.org/21/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
     * 
     * @param props
     */
    private void loadDefaultProducer(Properties props) {
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("delivery.timeout.ms", 30000);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
    }

    private void startConsumer() {
        String file = getConfig().getConfig(FILE_KAFKA_CONSUMER);
        if (Objects.isNull(file)) {
            file = getConfig().getConfig(Config.APP_CONF) + "/kafka-consumer.properties";
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            loadDefaultConsumer(props);
        }
        if (!props.containsKey("key.deserializer")) {
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        }
        if (!props.containsKey("value.deserializer")) {
            props.put("value.deserializer", TextMsgDeserializer.class.getName());
        }
        try {
            consumer = createConsumer(props);
            consumer.seekToEnd(Collections.emptyList());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            close();
        }
        final boolean autoCommit = "true".equals(props.get("enable.auto.commit")) ? true : false;
        this.dispatchT = new Thread(() -> {
            LOG.info("{} start", Thread.currentThread().getName());
            ConsumerRecords<String, Msg<?>> records = null;
            while (true) {
                if (closed) break;
                try {
                    records = consumer.poll(Duration.ofMillis(1000L));
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                    continue;
                }
                if (records == null) continue;

                records.forEach(record -> {
                    dispatch(record.value());
                });
                // commit if autoCommit is false
                if (!autoCommit) consumer.commitAsync();
            }
            consumer.close(Duration.ofSeconds(WAIT_CLOSED_SECOND)); // consumer close
            LOG.info("{} closed", Thread.currentThread().getName());
        }, "kafkaConsumeThread");
        dispatchT.start();
    }

    protected Consumer<String, Msg<?>> createConsumer(Properties props) {
        Consumer<String, Msg<?>> consumer = new KafkaConsumer<>(props);
        String topics = DEFAULT_TOPIC;
        if (getConfig().contain(D_KAFKA_SUBSCRIBE)) {
            topics = getConfig().getConfig(D_KAFKA_SUBSCRIBE);
            consumer.subscribe(Arrays.asList(topics.split("\\s+")));
        } else if (getConfig().contain(D_KAFKA_SUBSCRIBE_REGEX)) {
            topics = getConfig().getConfig(D_KAFKA_SUBSCRIBE_REGEX);
            consumer.subscribe(Pattern.compile(topics));
        } else {
            consumer.subscribe(Arrays.asList(topics));
        }
        LOG.info("startConsumer {} {}", props, topics);
        return consumer;
    }

    /**
     * http://kafka.apache.org/21/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
     * 
     * @param props
     */
    private void loadDefaultConsumer(Properties props) {
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "jframe");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
    }

    /*
     * (non-Javadoc)
     * @see jframe.core.dispatch.Dispatcher#receive(jframe.core.msg.Msg)
     */
    @Override
    public void receive(Msg<?> msg) {
        if (producer != null) {
            String topic = (String) msg.getMeta(D_KAFKA_R_TOPIC);
            if (Objects.isNull(topic)) {
                topic = DEFAULT_TOPIC;
            }
            Integer partition = partition(msg);
            Long timestamp = timestamp(msg);
            String key = (String) msg.getMeta(D_KAFKA_R_KEY);
            ProducerRecord<String, Msg<?>> record = new ProducerRecord<>(topic, partition, timestamp, key, msg, null);
            producer.send(record);
        }
    }

    private Long timestamp(Msg<?> msg) {
        Object ts = msg.getMeta(D_KAFKA_R_TIMESTAMP);
        if (ts == null) return null;
        if (ts instanceof Long) return (Long) ts;
        if (ts instanceof String) return Long.parseLong((String) ts);
        return null;
    }

    private Integer partition(Msg<?> msg) {
        Object p = msg.getMeta(D_KAFKA_R_PARTITION);
        if (p == null) return null;
        if (p instanceof Integer) return (Integer) p;
        if (p instanceof String) return Integer.parseInt((String) p);
        return null;
    }

    public Producer<String, Msg<?>> getProducer() {
        return producer;
    }

    public Consumer<String, Msg<?>> getConsumer() {
        return consumer;
    }

    @Override
    public void close() {
        // close producer
        if (enableProducer()) producer.close(WAIT_CLOSED_SECOND, TimeUnit.SECONDS);

        // close dispatcher and consumer
        if (enableConsumer()) {
            closed = true;
            if (dispatchT != null) {
                try {
                    dispatchT.join(WAIT_CLOSED_SECOND * 1000L);
                } catch (InterruptedException e) {}
            }
        }
        super.close();
    }

}
