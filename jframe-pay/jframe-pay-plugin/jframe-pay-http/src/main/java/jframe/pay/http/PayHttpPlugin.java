/**
 * 
 */
package jframe.pay.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import jframe.core.msg.Msg;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginSenderRecver;
import jframe.pay.http.handler.HttpServerInitializer;

/**
 * @author dzh
 * @date Jul 13, 2015 2:03:49 PM
 * @since 1.0
 */
public class PayHttpPlugin extends PluginSenderRecver {

    static final Logger LOG = LoggerFactory.getLogger(PayHttpPlugin.class);

    static final ExecutorService ES = new ThreadPoolExecutor(0, 200, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public Future<?> asyncExec(Runnable r) {
        return ES.submit(r);
    }

    public void start() throws PluginException {
        super.start();
        startHttpServer();
    }

    private EventLoopGroup bossGroup, workerGroup;

    private void startHttpServer() {
        try {
            int port = Integer.parseInt(getConfig(HttpConstants.HTTP_PORT, "8028"));
            int bossCount = Integer.parseInt(getConfig(HttpConstants.HTTP_BOSS_COUNT, "1"));
            int workCount = Integer.parseInt(getConfig(HttpConstants.HTTP_WORK_COUNT, "200"));

            SslContext sslCtx = null;
            if (isHttpsEnabled()) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            }

            LOG.info("Starting http server, listen on port->{}", port);

            bossGroup = new NioEventLoopGroup(bossCount);
            workerGroup = new NioEventLoopGroup(workCount);
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    // .handler(new LoggingHandler(LogLevel.ERROR))
                    .childHandler(new HttpServerInitializer(sslCtx));
            b.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_TIMEOUT, 3000)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000).option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_LINGER, 10).option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
            // .option(ChannelOption.ALLOCATOR,
            // PooledByteBufAllocator.DEFAULT)

            ;
            b.bind(port).syncUninterruptibly();
            LOG.info("Start http server successfully!");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            try {
                this.stop();
            } catch (PluginException e1) {
            }
        }
    }

    /**
     * default value is false
     * 
     * @return
     */
    private boolean isHttpsEnabled() {
        try {
            return Boolean.parseBoolean(getConfig(HttpConstants.HTTPS_ENABLED, "false"));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * jframe.core.plugin.PluginSenderRecver#canRecvMsg(jframe.core.msg.Msg)
     */
    @Override
    protected boolean canRecvMsg(Msg<?> arg0) {

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.plugin.PluginSenderRecver#doRecvMsg(jframe.core.msg.Msg)
     */
    @Override
    protected void doRecvMsg(Msg<?> arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.plugin.Plugin#stop()
     */
    public void stop() throws PluginException {
        super.stop();
        stopHttpServer();
        try {
            ES.shutdown();
            ES.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 
     */
    private void stopHttpServer() {
        if (bossGroup != null)
            bossGroup.shutdownGracefully();
        if (workerGroup != null)
            workerGroup.shutdownGracefully();
        LOG.info("Stop httpserver successfully!");
    }

}
