/**
 * 
 */
package jframe.httpclient.service.impl;

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
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		HttpClientConfig.init(plugin.getConfig(FILE_CONF));
		// SSLContext sslContext = SSLContexts.createSystemDefault();
		// SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		// sslContext,
		// SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER);
//		KeyStore myTrustStore = <...>
//		SSLContext sslContext = SSLContexts.custom()
//		        .useTLS()
//		        .loadTrustMaterial(myTrustStore)
//		        .build();
//		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
//		ConnectionSocketFactory plainsf = <...>
//		LayeredConnectionSocketFactory sslsf = <...>
//		Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
//		        .register("http", plainsf)
//		        .register("https", sslsf)
//		        .build();
//		HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
		
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		// Increase max total connection to 200
//		HttpClientConfig.getConf(key)
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);
		// Increase max connections for localhost:80 to 50
		HttpHost localhost = new HttpHost("localhost", 80);
		cm.setMaxPerRoute(new HttpRoute(localhost), 50);

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
				HttpHost target = (HttpHost) context
						.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
				if ("www.naughty-server.com".equalsIgnoreCase(target
						.getHostName())) {
					// Keep alive for 5 seconds only
					return 5 * 1000;
				} else {
					// otherwise keep alive for 30 seconds
					return 30 * 1000;
				}
			}

		};
		httpClient = HttpClients.custom().setConnectionManager(cm)
				.setKeepAliveStrategy(myStrategy).build();

		IdleConnectionMonitorThread = new IdleConnectionMonitorThread(cm);
		IdleConnectionMonitorThread.start();
		
		httpClient.execute(target, request, responseHandler)
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

	@Override
	public void send() {
		CloseableHttpResponse response = httpClient.execute(httpget, context);
		try {
			HttpEntity entity = response.getEntity();
		} finally {
			response.close();
		}
	}

	@Override
	public void sendOne() {

	}

	@Override
	public void sendRandom() {

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
						wait(5000);
						// Close expired connections
						connMgr.closeExpiredConnections();
						// Optionally, close connections
						// that have been idle longer than 30 sec
						connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
					}
				}
			} catch (InterruptedException ex) {
				// terminate
			}
		}

		public void shutdown() {
			shutdown = true;
			synchronized (this) {
				notifyAll();
			}
		}

	}

}
