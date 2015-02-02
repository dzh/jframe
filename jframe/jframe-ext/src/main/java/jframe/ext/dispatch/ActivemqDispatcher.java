/**
 * 
 */
package jframe.ext.dispatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import jframe.core.conf.Config;
import jframe.core.dispatch.AbstractDispatcher;
import jframe.core.msg.Msg;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 对于各种Msg类型的支持
 * <p>
 * 分发队列
 * <li>dispatch发送队列名由Msg的key定义->d.mq.send.queue</li>
 * <li>Msg没有定义发送队列,则默认队列名由MqConf的DefMqSendQueue定义</li>
 * <li>Msg没有定义接收队列，则默认队列由MqConf的DefMqRecvQueue定义</li>
 * </p>
 * 
 * @author dzh
 * @date Oct 15, 2014 4:35:17 PM
 * @since 1.0
 */
public class ActivemqDispatcher extends AbstractDispatcher {

	static Logger LOG = LoggerFactory.getLogger(ActivemqDispatcher.class);

	private PooledConnectionFactory poolFactory;

	private BlockingQueue<Msg<?>> _queue;

	static String MQ_FILE = "file.dispatcher.mq";

	static final Boolean NON_TRANSACTED = false;

	volatile boolean stop = false;

	/**
	 * 消息Key
	 */
	static String Msg_SendQueue = "d.mq.send.queue";

	// static String Msg_RecvQueue = "d.mq.recv.queue";

	public ActivemqDispatcher(String id, Config config) {
		super(id, config);
	}

	public void start() {
		if (!initMqConf()) {
			LOG.error("ActivemqDispatcher read conf error!");
			return;
		}
		LOG.info("mq producer connect to {}", MqConf.MqUrl);
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				MqConf.MqUrl);
		connectionFactory.setUseAsyncSend(MqConf.UseAsyncSend);
		poolFactory = new PooledConnectionFactory();
		poolFactory.setConnectionFactory(connectionFactory);
		poolFactory
				.setCreateConnectionOnStartup(MqConf.CreateConnectionOnStartup);
		poolFactory.setMaxConnections(MqConf.MaxConnections);
		poolFactory
				.setMaximumActiveSessionPerConnection(MqConf.MaximumActiveSessionPerConnection);
		poolFactory.start();

		_queue = new LinkedBlockingQueue<Msg<?>>();

		new Thread("DispatcherSendMqThread") {
			public void run() {
				LOG.info("DispatcherSendMqThread starting");

				final BlockingQueue<Msg<?>> queue = _queue;
				while (!stop) {
					try {
						sendMq(queue.take());

						Thread.sleep(6);
					} catch (Exception e) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}.start();

		new Thread("DispatcherRecvMqThread") {
			public void run() {
				LOG.info("DispatcherRecvMqThread starting");

				Connection connection = null;
				try {
					connection = poolFactory.createConnection();
					connection.start();

					Session session = connection.createSession(NON_TRANSACTED,
							Session.AUTO_ACKNOWLEDGE);
					Destination destination = session
							.createQueue(MqConf.DefMqRecvQueue);
					MessageConsumer consumer = session
							.createConsumer(destination);

					MsgTransfer msgTransfer = MqConf.Transfer;
					while (!stop) {
						Message message = consumer
								.receive(MqConf.ConsumerTimeout);
						if (message != null) {
							if (message instanceof TextMessage) {
								String text = ((TextMessage) message).getText();
								try {
									dispatch(msgTransfer.decode(text));

									Thread.sleep(6);
								} catch (Exception e) {
									LOG.error(e.getMessage());
								}
								if (LOG.isDebugEnabled()) {
									LOG.debug("Consume msg {}", text);
								}
							}
						}
					}
					consumer.close();
					session.close();
				} catch (Exception e) {
					LOG.error(e.getMessage());
				} finally {
					if (connection != null)
						try {
							connection.close();
						} catch (JMSException e) {
						}
				}

				if (!stop) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {

					}
					run();
				}
			}
		}.start();
		LOG.info("ActivemqDispatcher start successfully!");
	}

	private void sendMq(Msg<?> msg) {
		Connection connection = null;
		try {
			connection = poolFactory.createConnection();
			connection.start();

			Session session = connection.createSession(NON_TRANSACTED,
					Session.AUTO_ACKNOWLEDGE);
			Destination destination = session
					.createQueue(getSendQueueName(msg));
			MessageProducer producer = session.createProducer(destination);

			TextMessage message = session.createTextMessage(MqConf.Transfer
					.encode(msg));
			if (LOG.isDebugEnabled()) {
				LOG.debug("produce msg {}", message.getText());
			}
			producer.send(message);

			producer.close();
			session.close();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (JMSException e) {
				}
		}
	}

	public String getSendQueueName(Msg<?> msg) {
		String queue = msg.getMeta(Msg_SendQueue);
		return queue == null ? MqConf.DefMqSendQueue : queue;
	}

	// public String getRecvQueueName(Msg<?> msg) {
	// String queue = msg.getMeta(Msg_RecvQueue);
	// return queue == null ? MqConf.DefMqRecvQueue : queue;
	// }

	private boolean initMqConf() {
		Config config = getConfig();
		try {
			MqConf.init(config.getConfig(MQ_FILE));
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return false;
	}

	public void receive(final Msg<?> msg) {
		if (msg == null || stop)
			return;
		try {
			_queue.offer(msg, 60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
			// TODO 数据丢失问题
		}
	}

	/**
	 * 
	 */
	@Override
	public void close() {
		stop = true;
		super.close();
		if (poolFactory != null) {
			poolFactory.stop();
		}
		LOG.info("ActivemqDispatcher stopped!");
	}
}
