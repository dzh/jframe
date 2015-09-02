/**
 * 
 */
package jframe.pay.http.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Jul 25, 2014 9:04:17 AM
 * @since 1.0
 */
@Sharable
public class HttpReqDispatcher extends SimpleChannelInboundHandler<HttpObject> {

	private static final Logger LOG = LoggerFactory
			.getLogger(HttpReqDispatcher.class);

	/**
	 * <id,Dispatcher>
	 */
	private List<Dispatcher> _dispatcher = new LinkedList<Dispatcher>();

	/**
	 * <url,handler clazz>
	 */
	private Map<String, WeakReference<String>> _cache = new WeakHashMap<String, WeakReference<String>>();

	static String FILE_DISPATCHER = "dispatcher.xml";

	public HttpReqDispatcher() {
		loadDispatcher(FILE_DISPATCHER, _dispatcher);
	}

	public void loadDispatcher(String file, List<Dispatcher> dispatcher) {
		InputStream is = getClass().getResourceAsStream(file);
		if (is == null) {
			LOG.error("Not found dispatcher.xml");
			return;
		}

		_dispatcher.clear();
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(is);
			@SuppressWarnings("unchecked")
			List<Element> elements = doc.getRootElement()
					.elements("dispatcher");

			Dispatcher d = null;
			for (Element e : elements) {
				d = new Dispatcher();
				d.setId(e.attributeValue("id"));
				d.setUrl(e.elementText("url-pattern"));
				d.setClazz(e.elementText("handler"));
				dispatcher.add(d);
			}
		} catch (DocumentException e) {
			LOG.error(e.getMessage());
		} finally {
			try {
				is.close();
				reader.resetHandlers();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
	}

	/**
	 * request regular expression
	 */
	// public final static Pattern Req_Regx = Pattern
	// .compile("\\/(.*?)\\/(.*?)(?:\\/(.*))?");

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			if (LOG.isDebugEnabled()) {
				LOG.debug("Dispatch req path {}", req.getUri());
			}

			// pay client req
			final String reqUrl = req.getUri();
			Map<String, WeakReference<String>> cache = _cache;

			WeakReference<String> wr = cache.get(reqUrl);
			String clazz = wr != null && wr.get() != null ? wr.get() : null;
			if (clazz != null) {
				// if (reqUrl.startsWith("/common/image/download")) {
				// ctx.pipeline().addLast("http aggregator",
				// new HttpObjectAggregator(65536));
				// ctx.pipeline().addLast("http chunk",
				// new ChunkedWriteHandler());
				// }

				ctx.pipeline().addLast(
						(ChannelHandler) getClass().getClassLoader()
								.loadClass(clazz).newInstance());
			} else {
				List<Dispatcher> list = _dispatcher;
				boolean notFound = true;
				for (Dispatcher d : list) {
					if (Pattern.matches(d.getUrl(), reqUrl)) {
						// if (d.getId().equals("img.down")) {
						// ctx.pipeline().remove("http compressor");
						// ctx.pipeline().addLast("http aggregator",
						// new HttpObjectAggregator(65536)); //
						// ctx.pipeline().addLast("http chunk",
						// new ChunkedWriteHandler());
						// }
						ctx.pipeline().addLast(
								(ChannelHandler) getClass().getClassLoader()
										.loadClass(d.getClazz()).newInstance());
						cache.put(reqUrl,
								new WeakReference<String>(d.getClazz()));
						notFound = false;
						break;
					}
				}
				if (notFound) {
					ctx.pipeline().addLast(VoidHandler);
					cache.put(reqUrl, new WeakReference<String>(VoidHandler
							.getClass().getName()));
					LOG.error("Not found reqUrl->{}", reqUrl);
				}
			}
		}
		ReferenceCountUtil.retain(msg);
		ctx.fireChannelRead(msg);
	}

	static final VoidHandler VoidHandler = new VoidHandler();

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		LOG.error(cause.getMessage(), cause);
	}
}
