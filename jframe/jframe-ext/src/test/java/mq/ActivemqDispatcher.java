/**
 * 
 */
package mq;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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

	/**
	 * 发送消息持久化
	 */
	static String Msg_DeliveryMode = "d.mq.delivery.mode";

	// static String Msg_RecvQueue = "d.mq.recv.queue";
	private ExecutorService workPool;

	public ActivemqDispatcher(String id, Config config) {
		super(id, config);
	}

	public ActivemqDispatcher() {
		super("ActivemqDispatcher", null);
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

		workPool = new ThreadPoolExecutor(1, Runtime.getRuntime()
				.availableProcessors() + 1, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		new Thread("DispatcherSendMqThread") {
			public void run() {
				LOG.info("DispatcherSendMqThread starting");

				final BlockingQueue<Msg<?>> queue = _queue;
				while (!stop) {
					try {
						final Msg<?> msg = queue.take();
						if (msg != null) {
							workPool.execute(new Runnable() {
								public void run() {
									try {
										sendMq(msg);
										if (TestActiveMq.SUM - msg.getType() < 5)
											System.out
													.println("Finish send type ->"
															+ msg.getType()
															+ ", data->"
															+ new Date()
																	.toString());
									} catch (Exception e) {
										LOG.warn(e.getMessage());
									}
								}
							});
						}
						if (MqConf.SendSleepTime > 0)
							Thread.sleep(MqConf.SendSleepTime);
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
					final MessageConsumer consumer = session
							.createConsumer(destination);

					final MsgTransfer msgTransfer = MqConf.Transfer;
					while (!stop) {
						try {
							final Message message = consumer
									.receive(MqConf.ConsumerTimeout);
							if (message != null)
								workPool.execute(new Runnable() {
									public void run() {
										try {
											if (message instanceof TextMessage) {
												String text = ((TextMessage) message)
														.getText();
												dispatch(msgTransfer
														.decode(text));
												if (LOG.isDebugEnabled()) {
													LOG.debug("Consume msg {}",
															text);
												}
											}
										} catch (Exception e) {
											LOG.warn(e.getMessage());
										}
									}
								});
							if (MqConf.RecvSleepTime > 0)
								Thread.sleep(MqConf.RecvSleepTime);
//							Thread.sleep(2);
						} catch (Exception e) {
							LOG.error(e.getMessage());
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
			producer.setDeliveryMode(getDeliveryMode(msg));

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

	private int getDeliveryMode(Msg<?> msg) {
		try {
			if (msg.getMeta(Msg_DeliveryMode) == null) {
				return javax.jms.DeliveryMode.PERSISTENT;
			}
			return Integer.parseInt(msg.getMeta(Msg_DeliveryMode));
		} catch (Exception e) {

		}
		return javax.jms.DeliveryMode.PERSISTENT;
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
			MqConf.init("/home/dzh/git/jframe/jframe/jframe-ext/src/test/java/mq/d-activemq.properties");
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
		try {
			if (poolFactory != null) {
				poolFactory.stop();
			}

			if (workPool != null) {
				workPool.shutdown();
				workPool.awaitTermination(60, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
		}
		LOG.info("ActivemqDispatcher stopped!");
	}
}
