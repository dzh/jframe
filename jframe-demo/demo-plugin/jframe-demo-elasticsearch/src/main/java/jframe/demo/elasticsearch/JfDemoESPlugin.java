/**
 * 
 */
package jframe.demo.elasticsearch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.DefPlugin;
import jframe.core.plugin.PluginException;

/**
 * @author dzh
 * @date Sep 23, 2016 10:19:16 AM
 * @since 1.0
 */
public class JfDemoESPlugin extends DefPlugin {
    static Logger LOG = LoggerFactory.getLogger(JfDemoESPlugin.class);

    private NettyJaxrsServer netty;

    public RestClient client;

    static final ExecutorService ES = new ThreadPoolExecutor(0, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public Future<?> asyncExec(Runnable r) {
        return ES.submit(r);
    }

    public void start() throws PluginException {
        super.start();
        startHttpServer();
        startRestClient();
    }

    private void startRestClient() {
        try {
            HttpHost[] hosts = new HttpHost[] {
                    // new HttpHost("10.132.161.173", 30002, "http")
                    new HttpHost("127.0.0.1", 9200, "http") };
            client = RestClient.builder(hosts).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                    return requestConfigBuilder.setConnectTimeout(2000).setSocketTimeout(10000);
                }
            }).setMaxRetryTimeoutMillis(10000).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder.setMaxConnPerRoute(100).setMaxConnTotal(200);
                    // return httpClientBuilder
                    // .setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build());
                }
            }).build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    private void stopRestClient() {
        try {
            if (client != null)
                client.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    public void stop() throws PluginException {
        super.stop();
        stopHttpServer();
        stopRestClient();
    }

    private void startHttpServer() throws PluginException {
        try {
            int port = Integer.parseInt(getConfig(HttpConstants.HTTP_PORT, "8018"));
            String host = getConfig(HttpConstants.HTTP_HOST, "0.0.0.0");
            int bossCount = Integer.parseInt(getConfig(HttpConstants.HTTP_BOSS_COUNT, "-1"));
            bossCount = bossCount < 0 ? Runtime.getRuntime().availableProcessors() * 2 : bossCount;
            int workCount = Integer.parseInt(getConfig(HttpConstants.HTTP_WORK_COUNT, "200"));

            LOG.info("Starting http server, listen on port->{}", port);
            netty = new NettyJaxrsServer();
            netty.setIoWorkerCount(bossCount);
            netty.setExecutorThreadCount(workCount);

            ResteasyDeployment deployment = new ResteasyDeployment();
            deployment.setProviderFactory(new ResteasyProviderFactory());
            // deployment.getProviderFactory().register(ResteasyJacksonProvider.class);
            deployment.setApplication(new ESApplication());
            netty.setDeployment(deployment);
            netty.setHostname(host);
            netty.setPort(port);
            netty.setRootResourcePath(HttpConstants.PATH_ROOT);

            netty.setSecurityDomain(null);
            if (isHttpsEnabled()) {
                // SelfSignedCertificate ssc = new SelfSignedCertificate();
                // netty.setSSLContext(SslContextBuilder.forServer(ssc.certificate(),
                // ssc.privateKey()).build());
            }

            netty.start();
            LOG.info("Start http server successfully!");
        } catch (Exception e) {
            throw new PluginException(e.getCause());
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

    private void stopHttpServer() {
        if (netty != null) {
            netty.stop();
            netty = null;
        }
        LOG.info("Stop httpserver successfully!");
    }
}
