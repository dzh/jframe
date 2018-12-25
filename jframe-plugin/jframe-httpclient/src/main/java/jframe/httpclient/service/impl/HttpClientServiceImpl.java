/**
 * 
 */
package jframe.httpclient.service.impl;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.httpclient.HttpClientConfig;
import jframe.httpclient.HttpClientPlugin;
import jframe.httpclient.service.HttpClientService;

/**
 * 
 * @author dzh
 * @date Dec 2, 2014 12:11:23 PM
 * @since 1.0
 */
@Injector
public class HttpClientServiceImpl implements HttpClientService {

    HttpClientServiceImpl() {

    }

    static Logger LOG = LoggerFactory.getLogger(HttpClientServiceImpl.class);

    static String FILE_CONF = "file.httpclient";

    private CloseableHttpClient httpClient;

    private IdleConnectionMonitorThread IdleConnectionMonitorThread;

    @InjectPlugin
    static HttpClientPlugin plugin;

    static RequestConfig requestConfig;

    @Start
    public void start() {
        LOG.info("HttpClientServiceImpl is starting");
        try {
            HttpClientConfig.init(plugin.getConfig(FILE_CONF));
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(Integer.parseInt(HttpClientConfig.getConf(null, HttpClientConfig.HTTP_SO_TIMEOUT, "30000")))
                    .setConnectTimeout(Integer.parseInt(HttpClientConfig.getConf(null, HttpClientConfig.HTTP_CONN_TIMEOUT, "10000")))
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(Integer.parseInt(HttpClientConfig.getConf(null, HttpClientConfig.HTTP_MAX_CONN, "200")));
            cm.setDefaultMaxPerRoute(Integer.parseInt(HttpClientConfig.getConf(null, HttpClientConfig.HTTP_MAX_CONN_ROUTE, "60")));

            for (String host : HttpClientConfig.getHosts()) {
                String maxConn = HttpClientConfig.getConf(host, HttpClientConfig.HTTP_MAX_CONN, null);
                if (null == maxConn) {
                    continue;
                }
                HttpHost localhost = new HttpHost(HttpClientConfig.getConf(host, HttpClientConfig.IP),
                        Integer.parseInt(HttpClientConfig.getConf(host, HttpClientConfig.PORT, "80")));
                cm.setMaxPerRoute(new HttpRoute(localhost), Integer.parseInt(maxConn));
            }

            ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                    // Honor 'keep-alive' header
                    HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                    while (it.hasNext()) {
                        HeaderElement he = it.nextElement();
                        String param = he.getName();
                        String value = he.getValue();
                        if (value != null && param.equalsIgnoreCase("timeout")) {
                            try {
                                return Long.parseLong(value) * 1000;
                            } catch (NumberFormatException ignore) {}
                        }
                    }

                    String keepAlive = null;
                    HttpHost target = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
                    for (String host : HttpClientConfig.getHosts()) {
                        String ip = HttpClientConfig.getConf(host, HttpClientConfig.IP, "0");
                        if (target.getHostName().equals(ip)) {
                            keepAlive = HttpClientConfig.getConf(host, HttpClientConfig.HTTP_KEEP_ALIVE, null);
                            break;
                        }
                    }

                    if (keepAlive == null) {
                        keepAlive = HttpClientConfig.getConf(null, HttpClientConfig.HTTP_KEEP_ALIVE, "10");
                    }
                    return Integer.parseInt(keepAlive) * 1000;
                }

            };
            httpClient = HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(myStrategy).build();

            IdleConnectionMonitorThread = new IdleConnectionMonitorThread(cm);
            IdleConnectionMonitorThread.start();
        } catch (Exception e) {
            LOG.error("HttpClientServiceImpl init error {}!", e.getMessage());
        }
        LOG.info("HttpClientServiceImpl start succ");
    }

    @Stop
    public void stop() {
        if (IdleConnectionMonitorThread != null) {
            IdleConnectionMonitorThread.shutdown();
        }

        if (httpClient != null) try {
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
                        connMgr.closeIdleConnections(
                                Integer.parseInt(HttpClientConfig.getConf(null, HttpClientConfig.HTTP_IDLE_CONN_CLOSE, "30")),
                                TimeUnit.SECONDS);
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
    public String send(String id, String path, String data, Map<String, String> headers, Map<String, String> paras) throws Exception {
        LOG.debug("HttpClientServiceImpl.send {} {} {}", id, path, data);

        if (paras == null) paras = Collections.emptyMap();
        if (headers == null) headers = Collections.emptyMap();

        //
        String ip = paras.containsKey("ip") ? paras.get("ip") : HttpClientConfig.getConf(id, HttpClientConfig.IP);
        String port = paras.containsKey("port") ? paras.get("port") : HttpClientConfig.getConf(id, HttpClientConfig.PORT, "80");
        String scheme = HttpClientConfig.getConf(id, HttpClientConfig.SCHEME, HttpHost.DEFAULT_SCHEME_NAME);

        HttpHost target = new HttpHost(ip, Integer.parseInt(port), scheme);

        HttpRequestBase request;
        String mehtod = HttpClientConfig.getConf(id, HttpClientConfig.HTTP_METHOD, HttpClientConfig.M_POST);
        if (HttpClientConfig.M_GET.equals(mehtod)) {
            request = new HttpGet(target.toURI() + path + "?" + data);
        } else {
            request = new HttpPost(target.toURI() + path);
            String mimeType = paras.isEmpty() ? "text/plain" : paras.get(P_MIMETYPE);
            ((HttpPost) request).setEntity(
                    new StringEntity(data, ContentType.create(mimeType, HttpClientConfig.getConf(id, HttpClientConfig.HTTP_CHARSET))));
        }
        request.setConfig(requestConfig);

        if (headers != null) {
            for (String key : headers.keySet()) {
                request.addHeader(key, headers.get(key));
            }
        }

        CloseableHttpResponse rsp = null;
        try {
            rsp = httpClient.execute(request);
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

            HttpEntity entity = rsp.getEntity();
            ContentType contentType = ContentType.get(entity);
            if (contentType == null) {
                contentType = ContentType.APPLICATION_JSON;
            }

            Charset charset = paras.containsKey("rsp.charset") ? Charset.forName(paras.get("rsp.charset")) : contentType.getCharset();
            // // TODO decode by mime-type
            // if ("application/json".equalsIgnoreCase(contentType.getMimeType())) {
            // Reader reader = new InputStreamReader(entity.getContent(), charset == null ? Consts.UTF_8 : charset);
            // return GSON.fromJson(reader, new TypeToken<T>() {}.getType());
            // //
            // } else {
            // return (T) EntityUtils.toString(entity, charset);
            // }
            return EntityUtils.toString(entity, charset);
        } finally {
            if (rsp != null) {
                rsp.close();
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

    // @Override
    // public <T> T sendGroup(String gid, String path, String data, Map<String, String> headers, Map<String, String>
    // paras) throws Exception {
    // throw new Exception("not support this method");
    // }
    //
    // @Override
    // public <T> T sendRandom(String path, String data, Map<String, String> headers, Map<String, String> paras) throws
    // Exception {
    // throw new Exception("not support this method");
    // }
    //
    // @Override
    // public <T> T sendAll(String path, String data, Map<String, String> headers, Map<String, String> paras) throws
    // Exception {
    // throw new Exception("not support this method");
    // }

    public static HttpClientService testHttpClient(String httpclient) {
        try {
            HttpClientConfig.init(httpclient);
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
            cm.setMaxTotal(Integer.parseInt(HttpClientConfig.getConf(null, HttpClientConfig.HTTP_MAX_CONN, "200")));
            cm.setDefaultMaxPerRoute(Integer.parseInt(HttpClientConfig.getConf(null, HttpClientConfig.HTTP_MAX_CONN_ROUTE, "60")));

            for (String host : HttpClientConfig.getHosts()) {
                String maxConn = HttpClientConfig.getConf(host, HttpClientConfig.HTTP_MAX_CONN, null);
                if (null == maxConn) {
                    continue;
                }
                HttpHost localhost = new HttpHost(HttpClientConfig.getConf(host, HttpClientConfig.IP),
                        Integer.parseInt(HttpClientConfig.getConf(host, HttpClientConfig.PORT, "80")));
                cm.setMaxPerRoute(new HttpRoute(localhost), Integer.parseInt(maxConn));
            }

            // _httpClient.getParams().setIntParameter("http.socket.timeout",
            // 5000);

            ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                    // Honor 'keep-alive' header
                    HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                    while (it.hasNext()) {
                        HeaderElement he = it.nextElement();
                        String param = he.getName();
                        String value = he.getValue();
                        if (value != null && param.equalsIgnoreCase("timeout")) {
                            try {
                                return Long.parseLong(value) * 1000;
                            } catch (NumberFormatException ignore) {}
                        }
                    }

                    String keepAlive = null;
                    HttpHost target = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
                    for (String host : HttpClientConfig.getHosts()) {
                        String ip = HttpClientConfig.getConf(host, HttpClientConfig.IP, "0");
                        if (target.getHostName().equals(ip)) {
                            keepAlive = HttpClientConfig.getConf(host, HttpClientConfig.HTTP_KEEP_ALIVE, null);
                            break;
                        }
                    }

                    if (keepAlive == null) {
                        keepAlive = HttpClientConfig.getConf(null, HttpClientConfig.HTTP_KEEP_ALIVE, "10");
                    }
                    return Integer.parseInt(keepAlive) * 1000;
                }

            };
            // httpClient = HttpClients.custom().setConnectionManager(cm)
            // .setKeepAliveStrategy(myStrategy).build();
            //
            // IdleConnectionMonitorThread = new IdleConnectionMonitorThread(
            // cm);
            // IdleConnectionMonitorThread.start();
        } catch (Exception e) {
            LOG.error("HttpClientServiceImpl init error {}!", e.getMessage());
        }
        // return this;
        return null;
    }

}
