/**
 * 
 */
package es;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import jframe.demo.elasticsearch.weike.MemberDO;

/**
 * @author dzh
 * @date Sep 23, 2016 11:13:08 AM
 * @since 1.0
 */
@Ignore
public class TestRestClient {

    static Logger LOG = LoggerFactory.getLogger(TestRestClient.class);

    RestClient client;

    @Before
    public void init() {
        // final CredentialsProvider credentialsProvider = new
        // BasicCredentialsProvider();
        // credentialsProvider.setCredentials(AuthScope.ANY, new
        // UsernamePasswordCredentials("user", "password"));

        client = RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(30000);
            }
        }).setMaxRetryTimeoutMillis(30000)
                // .setHttpClientConfigCallback(new
                // RestClientBuilder.HttpClientConfigCallback() {
                // @Override
                // public HttpAsyncClientBuilder
                // customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder)
                // {
                // return httpClientBuilder
                // .setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build());
                // // .setDefaultCredentialsProvider(credentialsProvider);
                // }
                // })
                .build();

        // Credentials credentials = new UsernamePasswordCredentials("root",
        // "dzh");
        // client.getState().setCredentials(AuthScope.ANY, credentials);
    }

    @Test
    @Ignore
    public void testBulkTest() {
        // client.performRequest(method, endpoint, headers)
        List<Object> memList = new LinkedList<>();
        int i = 0;
        while (i < 1000) {
            MemberDO mem = new MemberDO();
            mem.setAvgPrice(1.0);
            mem.setBuyerId(1l);
            mem.setBuyerNick("戴忠");
            mem.setMobile("18616020610");
            mem.setEmail("dzh_11@qq.com");
            mem.setCity("上海");
            memList.add(mem);
            i++;
        }

        StringBuilder buf = new StringBuilder(1024);
        for (Object mem : memList) {
            buf.append("{\"index\": {}}");
            buf.append("\n");
            buf.append(Gson.toJson(mem));
            buf.append("\n");
        }
        // System.out.println(buf.toString());

        HttpEntity entity = new NStringEntity(buf.toString(), ContentType.APPLICATION_JSON);
        System.out.println(buf.length() * 2 / (1024));

        // indexData("/weike/member/_bulk", entity);
    }

    static Gson Gson = new Gson();

    @Test
    @Ignore
    public void indexTest() throws Exception {
        final MemberDO mem = new MemberDO();
        mem.setAvgPrice(1.0);
        mem.setBuyerId(1l);
        mem.setBuyerNick("戴忠");
        mem.setMobile("18616020610");
        mem.setEmail("dzh_11@qq.com");
        mem.setCity("上海");

        long start = System.currentTimeMillis();

        int count = 10000;
        AtomicLong sumTime = new AtomicLong(0);
        CountDownLatch latch = new CountDownLatch(count);
        final ExecutorService executor = Executors.newFixedThreadPool(100);
        while (count > 0) {
            executor.submit(() -> {
                long startTime = System.currentTimeMillis();
                String json = Gson.toJson(mem);

                HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
                String path = "/weike/member";
                // if (!"".equals(sellerId)) {
                // path += "?routing=" + sellerId;
                // }
                try {
                    Response indexResponse = client.performRequest("POST", path, Collections.<String, String> emptyMap(), entity);
                    long stopTime = System.currentTimeMillis() - startTime;
                    sumTime.addAndGet(stopTime);
                    LOG.info("indexMember {} - {}ms", json, stopTime);
                    LOG.info("indexResponse {}", indexResponse.toString());
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e.fillInStackTrace());
                } finally {
                    latch.countDown();
                }
            });
            count--;
        }

        latch.await();
        LOG.info("latch stop usage-{}ms sum-{}ms avg-{}ms", System.currentTimeMillis() - start, sumTime, sumTime.get() / 1000);
        executor.shutdownNow();
    }

    private void indexData(String path, HttpEntity entity) {
        try {
            Response indexResponse = client.performRequest("POST", path, Collections.<String, String> emptyMap(), entity);
            LOG.info(indexResponse.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    @After
    public void stop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReq() throws Exception {
        Response response = client.performRequest("GET", "/", Collections.singletonMap("pretty", "true"));
        System.out.println(EntityUtils.toString(response.getEntity()));

        // index a document
        HttpEntity entity = new NStringEntity("{\n" + "    \"user\" : \"kimchy\",\n" + "    \"post_date\" : \"2009-11-15T14:12:12\",\n"
                + "    \"message\" : \"trying out Elasticsearch\"\n" + "}", ContentType.APPLICATION_JSON);

        String u = URLEncoder.encode("root:dzh", "utf-8");
        BasicHeader auth = new BasicHeader("Authorization", "Basic " + u);
        Response indexResponse = client.performRequest("PUT", "/twitter/tweet/1", Collections.<String, String> emptyMap(), entity);

    }

    public void testReqAsync() {
        // int numRequests = 10;
        // final CountDownLatch latch = new CountDownLatch(numRequests);
        // for (int i = 0; i < numRequests; i++) {
        // client.performRequestAsync("PUT", "/twitter/tweet/" + i,
        // Collections.<String, String>emptyMap(),
        // // assume that the documents are stored in an entities array
        // entities[i], new ResponseListener() {
        // @Override
        // public void onSuccess(Response response) {
        // System.out.println(response);
        // latch.countDown();
        // }
        //
        // @Override
        // public void onFailure(Exception exception) {
        // latch.countDown();
        // }
        // });
        // }
        // // wait for all requests to be completed
        // latch.await();
    }

    @Test
    @Ignore
    public void testSniffer() {

    }

}
