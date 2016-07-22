/**
 * 
 */
package jframe.core.dispatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.msg.Msg;

/**
 * <p>
 * 
 * </p>
 * 
 * @ThreadSafe
 * @author dzh
 * @date Jun 18, 2013 4:21:18 PM
 */
public class DefDispatcher extends AbstractDispatcher {

    public DefDispatcher(String id, Config config) {
        super(id, config);
    }

    private static final Logger LOG = LoggerFactory.getLogger(DefDispatcher.class);

    private BlockingQueue<Msg<?>> _queue;

    private volatile boolean stop;

    private final CountDownLatch latch = new CountDownLatch(1);

    public static final Dispatcher newDispatcher(String id, Config conf) {
        return new DefDispatcher(id, conf);
    }

    private Thread disptchThread;

    public void start() {
        LOG.info("Dispatcher: " + getID() + " Starting!");
        stop = false;
        _queue = createDispatchQueue();
        initDispatchQueue(_queue);

        final long sleep = Integer.parseInt(getConfig().getConfig("DefDispatcher.sleep", "0"));
        disptchThread = new Thread("DispatchThread") { // 分发线程
            public void run() {
                try {
                    final BlockingQueue<Msg<?>> queue = _queue;
                    while (true) {
                        try {
                            if (stop || isInterrupted())
                                break;
                            dispatch(queue.take());

                            if (sleep > 0)
                                Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            LOG.warn(e.getMessage());
                            break;
                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e.fillInStackTrace());
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }
        };
        // disptchThread.setDaemon(true);
        disptchThread.start();
    }

    /**
     * 启动时，初始化分发队列
     * 
     * @param queue
     */
    void initDispatchQueue(BlockingQueue<Msg<?>> queue) {

    }

    /**
     * 关闭时，保存队列数据
     * 
     * @param _queue
     */
    void saveDispatchQueue(BlockingQueue<Msg<?>> queue) {

    }

    /**
     * 创建分发队列
     * 
     * @return
     */
    BlockingQueue<Msg<?>> createDispatchQueue() {
        return new LinkedBlockingQueue<Msg<?>>();
    }

    public void receive(Msg<?> msg) {
        if (msg == null || stop)
            return;
        try {
            _queue.offer(msg, 60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            // TODO 数据丢失问题
        }
        // LOG.debug("DefDispatcher receive msg " + msg.toString());
    }

    /**
     * 
     */
    @Override
    public void close() {
        if (stop)
            return;
        LOG.info("Dispacher: " + getID() + " Stopping!");
        closeDispatch();
        super.close();
        saveDispatchQueue(_queue);
    }

    /**
     * close DispatchThread
     */
    void closeDispatch() {
        stop = true;
        disptchThread.interrupt();
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

}
