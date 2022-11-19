package jframe.ext.dispatch.ons;

import com.aliyun.openservices.ons.api.*;
import jframe.core.conf.Config;
import jframe.core.dispatch.AbstractDispatcher;
import jframe.core.msg.Msg;
import jframe.ext.dispatch.rocketmq.RmqConst;
import jframe.ext.msg.MsgCodec;
import jframe.ext.msg.TextMsgCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * aliyun rocketmq
 * https://help.aliyun.com/document_detail/29547.html
 *
 * @author dzh
 * @date 2020/1/11 19:17
 */
public class OnsDispatcher extends AbstractDispatcher implements RmqConst {

    static Logger LOG = LoggerFactory.getLogger(OnsDispatcher.class);

    private Producer producer;
    private Consumer consumer;

    private MsgCodec msgCodec;

    public OnsDispatcher(String id, Config config) {
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

    //https://help.aliyun.com/document_detail/29547.html?spm=a2c4g.11186623.6.575.73a8d54cqwV0z7
    private void startProducer() {
        String file = getConfig().getConfig(FILE_RMQ_PRODUCER);
        if (Objects.isNull(file)) {
            file = getConfig().getConfig(Config.APP_CONF) + "/rmq-producer.properties";
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return;
        }

        LOG.info("start producer with {}", props);

//        Properties properties = new Properties();
//        // AccessKey 阿里云身份验证，在阿里云用户信息管理控制台获取
//        properties.put(PropertyKeyConst.AccessKey, "XXX");
//        // SecretKey 阿里云身份验证，在阿里云用户信息管理控制台获取
//        properties.put(PropertyKeyConst.SecretKey, "XXX");
//        //设置发送超时时间，单位毫秒
//        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
//        // 设置 TCP 接入域名，进入控制台的实例详情页面的获取接入点信息区域查看
//        properties.put(PropertyKeyConst.NAMESRV_ADDR,
//                "XXX");
        producer = ONSFactory.createProducer(props);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
        producer.start();
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
            return;
        }
        LOG.info("start consumer with {}", props);
        try {
            consumer = createConsumer(props);
            consumer.start();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
//            close();
        }
    }

    //https://help.aliyun.com/document_detail/29551.html?spm=a2c4g.11186623.6.581.7c13d54cmyIEny
    private Consumer createConsumer(Properties props) {
//        Properties properties = new Properties();
//        // 您在控制台创建的 Group ID
//        properties.put(PropertyKeyConst.GROUP_ID, "XXX");
//        // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
//        properties.put(PropertyKeyConst.AccessKey, "XXX");
//        // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
//        properties.put(PropertyKeyConst.SecretKey, "XXX");
//        // 设置 TCP 接入域名，进入控制台的实例管理页面的“获取接入点信息”区域查看
//        properties.put(PropertyKeyConst.NAMESRV_ADDR,
//                "XXX");
        // 集群订阅方式 (默认)
//         properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        // 广播订阅方式
        // properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);

        Consumer consumer = ONSFactory.createConsumer(props);

        boolean ingoreTimeoutMsg = Boolean.parseBoolean(props.getProperty("consume.ignore.timeout.msg", "false"));
        int ingoreTimeoutMsgMs = Integer.parseInt(props.getProperty("consume.ignore.timeout.msg.ms", "10000"));

        //订阅多个 Tag
        consumer.subscribe(props.getProperty("consume.topic", "jframe"),
                props.getProperty("consume.subExpression", "*"), new MessageListener() {
                    public Action consume(Message msg, ConsumeContext context) {
                        LOG.debug("consume ons msg-{}", msg);
                        try {
                            if (ingoreTimeoutMsg && (System.currentTimeMillis() - msg.getBornTimestamp()) >= ingoreTimeoutMsgMs) {
                                LOG.warn("discard ons timeout msg-{}", msg);
                                return Action.CommitMessage;
                            }
                            LOG.info("dispatch ons msg-{}", msg.getKey());
                            dispatch(msgCodec.decode(msg.getBody()));
                        } catch (Exception e) {
                            LOG.info(e.getMessage(), e);
                            return Action.ReconsumeLater;
                        }
                        return Action.CommitMessage;
                    }
                });
        return consumer;
    }

    private void initMsgCodec() {
        String clazz = getConfig().getConfig(M_RMQ_CODEC, TextMsgCodec.class.getName());
        try {
            msgCodec = (MsgCodec) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void receive(Msg<?> msg) {
        if (producer != null) {
            String topic = (String) msg.getMeta(M_RMQ_TOPIC);
            if (Objects.isNull(topic)) {
                topic = DEFAULT_TOPIC;
            }

            try {
                Message rmqMsg = new Message(topic,
                        (String) msg.getMeta(M_RMQ_TAG), (String) msg.getMeta(M_RMQ_Key),
                        msgCodec.encode(msg));
                SendResult r = producer.send(rmqMsg);
                LOG.debug("send msg {}, sendResult {}", msg, r);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);//todo
            }
        }
    }

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
