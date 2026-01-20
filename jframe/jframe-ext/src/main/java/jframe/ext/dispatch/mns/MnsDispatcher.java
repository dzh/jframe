package jframe.ext.dispatch.mns;

import com.aliyun.mns.client.*;
import com.aliyun.mns.model.Message;
import jframe.core.conf.Config;
import jframe.core.dispatch.AbstractDispatcher;
import jframe.core.msg.Msg;
import jframe.ext.msg.MsgCodec;
import jframe.ext.msg.TextMsgCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * 限制 https://help.aliyun.com/document_detail/128343.html
 * 接入点 https://help.aliyun.com/document_detail/175569.html
 * http(s)://AccountId.mns.cn-shanghai.aliyuncs.com
 * http://AccountId.mns.cn-shanghai-internal.aliyuncs.com
 *
 * @author dzh
 * @date 2022/11/17 16:04
 */
public class MnsDispatcher extends AbstractDispatcher implements AsyncCallback<Message>, MnsConst {

    static Logger LOG = LoggerFactory.getLogger(MnsDispatcher.class);

    private MNSClient client;

    private MsgCodec msgCodec;

    private String queue; //queue name, 目前支持单个队列消费

    private volatile boolean closed;

    private Thread dispatchT; // consume dispatch thread

    public MnsDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    public void start() {
        closed = false;
        try {
            initMNSClient();
            initMsgCodec();
            if (enableConsumer()) startConsumer();
            if (enableProducer()) startProducer();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            close();
        }
    }

    private void initMsgCodec() {
        String clazz = getConfig().getConfig(M_MNS_CODEC, TextMsgCodec.class.getName());
        try {
            LOG.info("initMsgCodec {}", clazz);
            msgCodec = (MsgCodec) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void initMNSClient() {
        String file = getConfig().getConfig(FILE_MNS);
        if (Objects.isNull(file)) {
            file = getConfig().getConfig(Config.APP_CONF) + "/mns.properties";
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return;
        }
        LOG.info("initMNSClient {}", props);

        CloudAccount account = new CloudAccount(
                props.getProperty(MNS_ACCESSKEYID),
                props.getProperty(MNS_ACCESSKEYSECRET),
                props.getProperty(MNS_ACCOUNTENDPOINT));
        client = account.getMNSClient();
        this.queue = props.getProperty(MNS_QUEUE, DEFAULT_QUEUE);
    }

    private void startProducer() {

    }

    private void startConsumer() {
//        List<String> queues = Arrays.asList(getConfig().getConfig(MnsConst.MNS_QUEUES, "").split("\\s+"));
//        if (queues.isEmpty()) {
//            LOG.error("msg.queue is not defined. failed to create consumer");
//            return;
//        }
        this.dispatchT = new Thread(() -> {
            LOG.info("{} start", Thread.currentThread().getName());
            CloudQueue q = client.getQueueRef(this.queue);
            while (!closed) {
                try {
//                    for (String queue : queues) {
                    Message msg = q.popMessage(60);
                    if (msg != null) {
                        dispatch(msgCodec.decode(msg.getMessageBodyAsBytes()));
                        q.deleteMessage(msg.getReceiptHandle());
                    }
//                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            LOG.info("{} closed", Thread.currentThread().getName());
        }, "MnsConsumeThread");
        dispatchT.start();
    }

    @Override
    public void receive(Msg<?> msg) {
        if (enableProducer() && client != null && client.isOpen()) {
            String q = (String) msg.getMeta(M_MNS_QUEUE);
            if (Objects.isNull(q)) {
                q = this.queue; //default queue
            }
            try {
                AsyncResult<Message> r = client.getQueueRef(q).asyncPutMessage(new Message(msgCodec.encode(msg)), this);
//                client.getQueueRef(q).putMessage(new Message(msgCodec.encode(msg)));
                LOG.debug("send msg {}, result {}", msg, r);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);//todo
            }
        } else {
            LOG.error("discard msg {}", msg);
        }
    }

    @Override
    public void close() {
        if (closed) return;
        if (client != null) client.close();

        // close dispatcher and consumer
        if (enableConsumer()) {
            if (dispatchT != null) {
                try {
                    dispatchT.join(60 * 1000L);
                } catch (InterruptedException e) {
                }
            }
        }
        closed = true;
        super.close();
    }

    protected boolean enableProducer() {
        return true;
    }

    protected boolean enableConsumer() {
        return true;
    }

    @Override
    public void onSuccess(Message msg) {
        LOG.debug("mns put message {} successfully", msg);
    }

    @Override
    public void onFail(Exception e) {
        LOG.error(e.getMessage(), e);
    }
}
