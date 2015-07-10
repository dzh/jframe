/**
 * 
 */
package jframe.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import jframe.core.msg.Msg;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginSenderRecver;
import jframe.netty.http.handler.HttpServerInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Jul 10, 2015 1:06:01 PM
 * @since 1.0
 */
public class JframeHttpPlugin extends PluginSenderRecver {

	static final Logger LOG = LoggerFactory.getLogger(JframeHttpPlugin.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginSenderRecver#doRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected void doRecvMsg(Msg<?> msg) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.plugin.PluginSenderRecver#canRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected boolean canRecvMsg(Msg<?> msg) {
		// TODO Auto-generated method stub
		return false;
	}

	private EventLoopGroup bossGroup, workerGroup;

	private Channel srvCh;

	public void start() throws PluginException {
		super.start();
		startHttpServer();
	}

	/**
	 * 
	 */
	private void startHttpServer() {
		try {
			int port = Integer.parseInt(getConfig(HttpConstants.HTTP_PORT,
					"8018"));
			int bossCount = Integer.parseInt(getConfig(
					HttpConstants.HTTP_BOSS_COUNT, "1"));
			int workCount = Integer.parseInt(getConfig(
					HttpConstants.HTTP_WORK_COUNT, "200"));

			SslContext sslCtx = null;
			if (isHttpsEnabled()) {
				SelfSignedCertificate ssc = new SelfSignedCertificate();
				sslCtx = SslContext.newServerContext(ssc.certificate(),
						ssc.privateKey());
			}

			LOG.info("Starting http server, listen on port->{}", port);

			bossGroup = new NioEventLoopGroup(bossCount);
			workerGroup = new NioEventLoopGroup(workCount);
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					// .handler(new LoggingHandler(LogLevel.ERROR))
					.childHandler(new HttpServerInitializer(sslCtx));
			b.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_TIMEOUT, 3000)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.SO_LINGER, 10)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.TCP_NODELAY, true)
			// .option(ChannelOption.ALLOCATOR,
			// PooledByteBufAllocator.DEFAULT)

			;
			ChannelFuture future = b.bind(port);
			future.syncUninterruptibly();
			srvCh = future.channel();
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
			return Boolean.parseBoolean(getConfig(HttpConstants.HTTPS_ENABLED,
					"false"));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.Plugin#stop()
	 */
	public void stop() throws PluginException {
		super.stop();
		stopHttpServer();
	}

	/**
 * 
 */
	private void stopHttpServer() {
		if (srvCh != null)
			srvCh.close();
		if (bossGroup != null)
			bossGroup.shutdownGracefully();
		if (workerGroup != null)
			workerGroup.shutdownGracefully();
		LOG.info("Stop httpserver successfully!");
	}

}
