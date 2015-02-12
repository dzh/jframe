/**
 * 
 */
package jframe.httpclient.service.impl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.httpclient.HttpClientConfig;
import jframe.httpclient.HttpClientPlugin;
import jframe.httpclient.service.HttpClientService;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author dzh
 * @date Dec 2, 2014 12:11:23 PM
 * @since 1.0
 */
@Injector
public class HttpClientServiceImpl implements HttpClientService {

	static Logger LOG = LoggerFactory.getLogger(HttpClientServiceImpl.class);

	static String FILE_CONF = "file.httpclient";

	private CloseableHttpClient httpClient;

	private IdleConnectionMonitorThread IdleConnectionMonitorThread;

	@InjectPlugin
	static HttpClientPlugin plugin;

	@Start
	public void start() {
		try {
			HttpClientConfig.init(plugin.getConfig(FILE_CONF));
			// SSLContext sslContext = SSLContexts.createSystemDefault();
			// SSLConnectionSocketFactory sslsf = new
			// SSLConnectionSocketFactory(
			// sslContext,
			// SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER);
			// KeyStore myTrustStore = <...>
			// SSLContext sslContext = SSLContexts.custom()
			// .useTLS()
			// .loadTrustMaterial(myTrustStore)
			// .build();
			// SSLConnectionSocketFactory sslsf = new
			// SSLConnectionSocketFactory(sslContext);
			// ConnectionSocketFactory plainsf = <...>
			// LayeredConnectionSocketFactory sslsf = <...>
			// Registry<ConnectionSocketFactory> r =
			// RegistryBuilder.<ConnectionSocketFactory>create()
			// .register("http", plainsf)
			// .register("https", sslsf)
			// .build();
			// HttpClientConnectionManager cm = new
			// PoolingHttpClientConnectionManager(r);

			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(Integer.parseInt(HttpClientConfig.getConf(null,
					HttpClientConfig.HTTP_MAX_CONN, "200")));
			cm.setDefaultMaxPerRoute(Integer.parseInt(HttpClientConfig.getConf(
					null, HttpClientConfig.HTTP_MAX_CONN_ROUTE, "60")));

			for (String host : HttpClientConfig.getHosts()) {
				String maxConn = HttpClientConfig.getConf(host,
						HttpClientConfig.HTTP_MAX_CONN, null);
				if (null == maxConn) {
					continue;
				}
				HttpHost localhost = new HttpHost(HttpClientConfig.getConf(
						host, HttpClientConfig.IP),
						Integer.parseInt(HttpClientConfig.getConf(host,
								HttpClientConfig.PORT, "80")));
				cm.setMaxPerRoute(new HttpRoute(localhost),
						Integer.parseInt(maxConn));
			}

			// _httpClient.getParams().setIntParameter("http.socket.timeout",
			// 5000);

			ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
				public long getKeepAliveDuration(HttpResponse response,
						HttpContext context) {
					// Honor 'keep-alive' header
					HeaderElementIterator it = new BasicHeaderElementIterator(
							response.headerIterator(HTTP.CONN_KEEP_ALIVE));
					while (it.hasNext()) {
						HeaderElement he = it.nextElement();
						String param = he.getName();
						String value = he.getValue();
						if (value != null && param.equalsIgnoreCase("timeout")) {
							try {
								return Long.parseLong(value) * 1000;
							} catch (NumberFormatException ignore) {
							}
						}
					}

					String keepAlive = null;
					HttpHost target = (HttpHost) context
							.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
					for (String host : HttpClientConfig.getHosts()) {
						String ip = HttpClientConfig.getConf(host,
								HttpClientConfig.IP, "0");
						if (target.getHostName().equals(ip)) {
							keepAlive = HttpClientConfig.getConf(host,
									HttpClientConfig.HTTP_KEEP_ALIVE, null);
							break;
						}
					}

					if (keepAlive == null) {
						keepAlive = HttpClientConfig.getConf(null,
								HttpClientConfig.HTTP_KEEP_ALIVE, "10");
					}
					return Integer.parseInt(keepAlive) * 1000;
				}

			};
			httpClient = HttpClients.custom().setConnectionManager(cm)
					.setKeepAliveStrategy(myStrategy).build();

			IdleConnectionMonitorThread = new IdleConnectionMonitorThread(cm);
			IdleConnectionMonitorThread.start();
		} catch (Exception e) {
			LOG.error("HttpClientServiceImpl init error {}!", e.getMessage());
		}
	}

	@Stop
	public void stop() {
		if (IdleConnectionMonitorThread != null) {
			IdleConnectionMonitorThread.shutdown();
		}

		if (httpClient != null)
			try {
				httpClient.close();
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		LOG.info("HttpClientServiceImpl closed!");
	}

	public static class IdleConnectionMonitorThread extends Thread {

		private final HttpClientConnectionManager connMgr;
		private volatile boolean shutdown;

		public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
			super();
			this.connMgr = connMgr;
		}

		@Override
		public void run() {

			try {
				while (!shutdown) {
					synchronized (this) {
						wait(5000); // TODO
						// Close expired connections
						connMgr.closeExpiredConnections();
						// Optionally, close connections
						// that have been idle longer than 30 sec
						connMgr.closeIdleConnections(Integer
								.parseInt(HttpClientConfig.getConf(null,
										HttpClientConfig.HTTP_IDLE_CONN_CLOSE,
										"30")), TimeUnit.SECONDS);
					}
				}
			} catch (InterruptedException ex) {
				LOG.error(ex.getMessage());
			}
		}

		public void shutdown() {
			shutdown = true;
			synchronized (this) {
				notifyAll();
			}
		}

	}

	@Override
	public <T> T send(String id, String path, String data,
			Map<String, String> headers, Map<String, String> paras)
			throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("HttpClientServiceImpl.send {} {}", id, data);
		}

		HttpHost target = new HttpHost(HttpClientConfig.getConf(id,
				HttpClientConfig.IP), Integer.parseInt(HttpClientConfig
				.getConf(id, HttpClientConfig.PORT)),
				HttpHost.DEFAULT_SCHEME_NAME);

		HttpUriRequest request;
		String mehtod = HttpClientConfig.getConf(id,
				HttpClientConfig.HTTP_METHOD, HttpClientConfig.M_POST);
		if (HttpClientConfig.M_GET.equals(mehtod)) {
			request = new HttpGet(target.toURI() + path + "?" + data);
		} else {
			request = new HttpPost(target.toURI() + path);
			String mimeType = paras == null ? "text/plain" : paras
					.get(P_MIMETYPE);
			((HttpPost) request).setEntity(new StringEntity(data, ContentType
					.create(mimeType, HttpClientConfig.getConf(id,
							HttpClientConfig.HTTP_CHARSET))));
		}

		if (headers != null) {
			for (String key : headers.keySet()) {
				request.addHeader(key, headers.get(key));
			}
		}

		CloseableHttpResponse resp = null;
		try {
			// ResponseHandler<String> responseHandler = new
			// ResponseHandler<String>() {
			//
			// @Override
			// public String handleResponse(
			// final HttpResponse response) throws ClientProtocolException,
			// IOException {
			// int status = response.getStatusLine().getStatusCode();
			// if (status >= 200 && status < 300) {
			// HttpEntity entity = response.getEntity();
			// return entity != null ? EntityUtils.toString(entity) : null;
			// } else {
			// throw new ClientProtocolException("Unexpected response status: "
			// + status);
			// }
			// }
			//
			// };
			resp = httpClient.execute(request);
			HttpEntity entity = resp.getEntity();
			ContentType contentType = ContentType.getOrDefault(entity);
			Charset charset = contentType.getCharset();
			Reader reader = new InputStreamReader(entity.getContent(), charset);
			Type type = new TypeToken<T>() {
			}.getType();
			return GSON.fromJson(reader, type);
		} finally {
			if (resp != null) {
				EntityUtils.consume(resp.getEntity());
			}
		}
	}

	static final Gson GSON = new GsonBuilder().create();

	//
	// public static final String toJson(Object obj) {
	// return gson.toJson(obj);
	// }
	//
	// public static final <T> T fromJson(String json, Class<T> clazz) {
	// return gson.fromJson(json, clazz);
	// }

	@Override
	public <T> T sendGroup(String gid, String path, String data,
			Map<String, String> headers, Map<String, String> paras)
			throws Exception {
		throw new Exception("");
	}

	@Override
	public <T> T sendRandom(String path, String data,
			Map<String, String> headers, Map<String, String> paras)
			throws Exception {
		throw new Exception("");
	}

	@Override
	public <T> T sendAll(String path, String data, Map<String, String> headers,
			Map<String, String> paras) throws Exception {
		throw new Exception("");
	}

}
