/**
 * 
 */
package jframe.elasticsearch.service.impl;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.elasticsearch.ElasticSearchPlugin;
import jframe.elasticsearch.service.ElasticRestClient;

/**
 * @author dzh
 * @date Oct 8, 2016 7:00:09 PM
 * @since 1.0
 */
@Injector
public class ElasticRestClientImpl implements ElasticRestClient {

    static Logger LOG = LoggerFactory.getLogger(ElasticRestClientImpl.class);

    @InjectPlugin
    static ElasticSearchPlugin Plugin;

    private RestClient client;

    @Start
    void start() {
        String[] hosts = Plugin.getConfig(HTTP_HOST, "localhost:9200").split(" ");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; ++i) {
            httpHosts[i] = HttpHost.create(hosts[i]);
        }
        client = RestClient.builder(httpHosts).setRequestConfigCallback(new CustomRequesetConfig()).build();
        LOG.info("{} start succ", ElasticRestClientImpl.class.getName());
    }

    public RestClient client() {
        return client;
    }

    @Stop
    void stop() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    class CustomRequesetConfig implements RequestConfigCallback {

        @Override
        public Builder customizeRequestConfig(Builder requestConfigBuilder) {
            int sTimtout = Integer.parseInt(Plugin.getConfig(HTTP_SOCKET_TIMEOUT, "30000"));
            int cTimeout = Integer.parseInt(Plugin.getConfig(HTTP_CONN_TIMEOUT, "1000"));
            return requestConfigBuilder.setSocketTimeout(sTimtout).setConnectTimeout(cTimeout);
        }

    }

}
