package jframe.elasticsearch.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date May 17, 2018 1:39:51 PM
 * @version 0.0.1
 */
public class TestRestClient {

    static Logger LOG = LoggerFactory.getLogger(TestRestClient.class);

    static RestClient client;

    @BeforeClass
    public static void init() {
        HttpHost host = HttpHost.create("127.0.0.1:9200");
        client = RestClient.builder(host).build();
    }

    @Test
    public void testIndex() throws IOException {
        Response r = client.performRequest("GET", "/_nodes/_local", new BasicHeader[0]);
        LOG.info("{}", EntityUtils.toString(r.getEntity(), StandardCharsets.UTF_8));
    }

    @AfterClass
    public static void stop() {
        try {
            client.close();
        } catch (IOException e) {}
    }

}
