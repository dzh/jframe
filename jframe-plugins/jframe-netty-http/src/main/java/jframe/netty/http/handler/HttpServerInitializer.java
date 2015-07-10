package jframe.netty.http.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author dzh
 * @date Jul 25, 2014 1:24:54 PM
 * @since 1.0
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	static Logger LOG = LoggerFactory.getLogger(HttpServerInitializer.class);

	private SslContext sslCtx;

	private HttpReqDispatcher reqDis = new HttpReqDispatcher();

	public HttpServerInitializer() {
		this(null);
	}

	public HttpServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		// p.addLast("log", new LoggingHandler(LogLevel.ERROR));

		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		p.addLast(new HttpRequestDecoder());
		p.addLast(new HttpResponseEncoder());
		p.addLast("http compressor", new HttpContentCompressor());
		// p.addLast(new HttpObjectAggregator(1048576));
		p.addLast("http dispatcher", reqDis);
		p.addLast("idleStateHandler", new IdleStateHandler(10, 10, 0));
		p.addLast("heartbeatHandler", new HeartbeatHandler());
	}

	@Sharable
	public class HeartbeatHandler extends ChannelDuplexHandler {
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
				throws Exception {
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent e = (IdleStateEvent) evt;
				if (e.state() == IdleState.ALL_IDLE) {
					ctx.close();
					LOG.info("close ALL_IDLE {}", evt.toString());
				} else if (e.state() == IdleState.READER_IDLE) {
					ctx.close();
					LOG.info("close READER_IDLE {}", evt.toString());
				} else if (e.state() == IdleState.WRITER_IDLE) {
					ctx.close();
					LOG.info("close WRITER_IDLE {}", evt.toString());
				}
			}
		}
	}
}
