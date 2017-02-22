/**
 * 
 */
package jframe.example.plugin;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginSenderRecver;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Message;
import jframe.core.plugin.annotation.MsgInterest;
import jframe.core.plugin.annotation.Plugin;

/**
 * @author dzh
 * @date Sep 18, 2013 4:14:07 PM
 * @since 1.0
 */
@Injector
@Plugin(startOrder = 2, stopOrder = 6)
@Message(isSender = true, isRecver = true, msgTypes = { 1, 2 }, recvConfig = true, recvPoison = true)
public class ExamplePlugin extends PluginSenderRecver {

    private static final Logger LOG = LoggerFactory.getLogger(ExamplePlugin.class);

    private BlockingQueue<Msg<?>> _queue = new LinkedBlockingQueue<Msg<?>>();

    ScheduledExecutorService threads = Executors.newScheduledThreadPool(2);

    public void start() throws PluginException {
        super.start();
        // threads.scheduleAtFixedRate(() -> {
        // try {
        // send(new TextMsg().setType(randomType(3)).setValue("ExamplePlugin-" +
        // new Date().toString()));
        // } catch (Exception e) {
        // LOG.error(e.getMessage());
        // }
        // }, 0, 6, TimeUnit.SECONDS);

        threads.scheduleAtFixedRate(() -> {
            try {
                Msg<?> msg = null;
                try {
                    msg = _queue.poll(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    LOG.warn(e.getMessage());
                }
                if (msg == null)
                    return;

                LOG.info(msg.toString());
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public int randomType(int max) {
        return new Random().nextInt(max) % max + 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.plugin.Plugin#stop()
     */
    public void stop() throws PluginException {
        super.stop();
        threads.shutdownNow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.plugin.Plugin#destroy()
     */
    public void destroy() throws PluginException {
        super.destroy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.plugin.PluginSenderRecver#doRecvMsg(jframe.core.msg.Msg)
     */
    @Override
    protected void doRecvMsg(Msg<?> msg) {
        try {
            _queue.offer(msg, 2, TimeUnit.SECONDS); //
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写这个方法，因为默认插件不接收自己的消息
     */
    @MsgInterest
    public boolean interestMsg(Msg<?> msg) {
        if (msg == null || PluginStatus.STOP == getStatus())
            return false;
        return canRecvMsg(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * jframe.core.plugin.PluginSenderRecver#canRecvMsg(jframe.core.msg.Msg)
     */
    @Override
    protected boolean canRecvMsg(Msg<?> msg) {
        return true;
    }

    public String toString() {
        return "ExamplePlugin";
    }

}
