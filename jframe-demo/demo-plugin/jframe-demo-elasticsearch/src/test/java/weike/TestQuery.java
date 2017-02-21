/**
 * 
 */
package weike;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
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

/**
 * @author dzh
 * @date Sep 29, 2016 2:46:23 PM
 * @since 1.0
 */
public class TestQuery {
    static Logger LOG = LoggerFactory.getLogger(TestQuery.class);

    RestClient client;

    @Before
    public void init() {
        HttpHost host = new HttpHost("121.199.167.226", 30002, "http");
        // HttpHost host = new HttpHost("127.0.0.1", 9002, "http");
        client = RestClient.builder(host).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(30000);
            }
        })
                // .setMaxRetryTimeoutMillis(30000)
                // .setHttpClientConfigCallback(new
                // RestClientBuilder.HttpClientConfigCallback() {
                // @Override
                // public HttpAsyncClientBuilder
                // customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder)
                // {
                // return httpClientBuilder
                // .setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build());
                // }
                // })
                .build();
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
    @Ignore
    public void testIndex() throws Exception {
        int number_of_shards = 3;
        int number_of_replicas = 1;
        // query("DELETE", null, "/index2", "delete");
        // add
        String json = "{\"settings\":{\"index\":{\"number_of_shards\" : " + number_of_shards + ",\"number_of_replicas\" : " + number_of_replicas
                + "}}}";
        query("PUT", json, "/index1", "add");
        // get _settings, _mappings, _warmers and _aliases
        query("GET", null, "/index1", "get");
        query("GET", null, "/index1/_settings", "get");

        // exist
        query("HEAD", null, "/index1", "exist");
        // open
        query("POST", null, "/index1/_open", "open");
        // close
        query("POST", null, "/index1/_close", "close");
        query("HEAD", null, "/index1", "exist");
        // delete
        query("DELETE", null, "/index1", "delete");
        query("HEAD", null, "/index1", "exist");
        query("GET", null, "/index1", "get");
        //
    }

    @Test
    @Ignore
    public void testAutoIndex() throws Exception {

    }

    @Test
    public void testIndexAlias() throws Exception {
        int number_of_shards = 3;
        int number_of_replicas = 1;
        // add index
        String json = "{\"settings\":{\"index\":{\"number_of_shards\" : " + number_of_shards + ",\"number_of_replicas\" : " + number_of_replicas
                + "}}}";
        // query("DELETE", null, "/test1", "delete");
        // query("DELETE", null, "/test2", "delete");
        query("PUT", json, "/test1", "add");
        // query("PUT", json, "/test2", "add");
        // add
        json = "{\"actions\" : ["
                // + "{ \"remove\" : { \"index\" : \"test1\", \"alias\" :
                // \"alias1\" } },"
                + "{ \"add\" : { \"index\" : \"test1\", \"alias\" : \"alias1\" ,\"routing\" : \"1\"} }" + "]}";
        query("POST", json, "/_aliases", "add");
        query("POST", json, "/_aliases", "add");
        // get
        query("GET", null, "/test1/_aliases/alias1", "get");
        // exist
        query("HEAD", null, "/test1/_aliases/alias1", "exist");
        // update
        json = "{\"actions\" : [" + "{ \"remove\" : { \"index\" : \"test1\", \"alias\" :\"alias1\" } },"
                + "{ \"add\" : { \"index\" : \"test1\", \"alias\" : \"alias2\" ,\"index_routing\" : \"2\",\"search_routing\" : \"1\"} }" + "]}";
        query("POST", json, "/_aliases", "update");
        query("GET", null, "/_aliases/alias2", "get");
        query("HEAD", null, "/*/_aliases/alias2", "exist");
        // delete curl -XDELETE 'localhost:9200/users/_alias/user_12'
        query("DELETE", null, "/_all/_aliases/alias2", "delete");
        query("GET", null, "/_aliases/alias2", "get");
        // delete index
        query("DELETE", null, "/test1", "delete");

        //
        query("GET", null, "/_aliases/alias2", "get");
        query("HEAD", null, "/_all/_aliases/alias2", "exist");
    }

    @Test
    @Ignore
    public void testCount() throws Exception {
        // JsonObject json = new JsonObject();
        // json.addProperty("explain", true);
        // JsonObject query = new JsonObject();
        // JsonObject term = new JsonObject();
        // term.addProperty("sellerId", 807426238);
        // query.add("term", term);
        // json.add("query", query);
        // XContentBuilder json = XContentFactory.jsonBuilder();
        // json.startObject().field("explain", true).field("query",
        // XContentFactory.jsonBuilder().);

        String json = "{\"explain\":true,\"query\":{\"range\":{\"tradeAmount\":{\"gte\":10,\"lte\":500}}}}";
        json = "{\"query\" : {\"term\" : { \"sellerId\" :  897258160}}}";

        long startTime = System.currentTimeMillis();
        HttpEntity entity = new NStringEntity(json.toString(), ContentType.APPLICATION_JSON);
        Response response = client.performRequest("GET", "/weike/member/_count", Collections.singletonMap("pretty", "true"), entity);

        // LOG.info(XContentFactory.jsonBuilder().startObject().field("gender",
        // "male").endObject().string());
        LOG.info("count-{} {}ms", EntityUtils.toString(response.getEntity()), System.currentTimeMillis() - startTime);

        json = "{\"actions\" : ["
                // + "{ \"remove\" : { \"index\" : \"test1\", \"alias\" :
                // \"alias1\" } },"
                + "{ \"add\" : { \"index\" : \"weike\", \"alias\" : \"wkalias1\"} }" + "]}";
        query("POST", json, "/_aliases", "add");
        query("GET", null, "/_aliases/wkalias1", "get");

        json = "{\"query\" : {\"term\" : { \"sellerId\" :  897258160}}}";
        query("GET", json, "/wkalias1/member/_count", "alias count");

        query("DELETE", null, "/_all/_aliases/wkalias1", "delete");
    }

    @Test
    @Ignore
    public void testSearch() throws Exception {
        // JsonObject json = new JsonObject();
        // json.addProperty("from", "0");
        // json.addProperty("size", "10");
        // json.addProperty("explain", true);
        // JsonObject query = new JsonObject();
        // query.add
        // json.add("query", query);
        String json = "{\"explain\":false,\"from\":0,\"size\":1,\"query\":{\"range\":{\"tradeAmount\":{\"gte\":10,\"lte\":2000}}}}";

        long startTime = System.currentTimeMillis();
        HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
        Response response = client.performRequest("GET", "/weike/member/_search", Collections.singletonMap("pretty", "true"), entity);
        LOG.info("search-{} {}ms", EntityUtils.toString(response.getEntity()), System.currentTimeMillis() - startTime);
    }

    static Gson Gson = new Gson();

    String query(String method, String json, String path, String log) throws IOException {
        long startTime = System.currentTimeMillis();
        HttpEntity entity = null;
        if (json != null)
            entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
        Response response = client.performRequest(method, path, Collections.singletonMap("pretty", "true"), entity);
        String result = null;
        if (response.getEntity() != null)
            result = EntityUtils.toString(response.getEntity());
        LOG.info("{} rsp-{} {} {}ms", log, response.getStatusLine().getStatusCode(), result, System.currentTimeMillis() - startTime);
        return result;
    }

    @Test
    public void testScollSearch() throws Exception {
        // 1
        String json = "{\"explain\":false,\"from\":0,\"size\":2,\"query\":{\"range\":{\"tradeAmount\":{\"gte\":10,\"lte\":2000}}},\"sort\":[\"_doc\"]}";
        String result = query("GET", json, "/weike/member/_search?scroll=1m", "s0");

        // scroll
        Map<String, String> rj = Gson.fromJson(result, HashMap.class);
        String scrollId = rj.get("_scroll_id");
        json = "{\"scroll_id\":\"" + scrollId + "\"}";
        result = query("GET", json, "/_search/scroll?scroll=1m", "s1-" + scrollId);

        // scroll
        rj = Gson.fromJson(result, HashMap.class);
        scrollId = rj.get("_scroll_id");
        json = "{\"scroll_id\":\"" + scrollId + "\"}";
        result = query("GET", json, "/_search/scroll?scroll=1m", "s2-" + scrollId);

        // clear
        rj = Gson.fromJson(result, HashMap.class);
        scrollId = rj.get("_scroll_id");
        json = "{\"scroll_id\":[\"" + scrollId + "\"]}";
        query("DELETE", json, "/_search/scroll", "c-" + scrollId);
    }

    @Test
    @Ignore
    public void testDeleteScoll() throws IOException {
        String json = "{\"explain\":false,\"scroll_id\":[\"\",\"\"]}";

        long startTime = System.currentTimeMillis();
        HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
        Response response = client.performRequest("DELETE", "/weike/member/_search/scroll", Collections.singletonMap("pretty", "true"), entity);
        LOG.info("scroll search-{} {}ms", EntityUtils.toString(response.getEntity()), System.currentTimeMillis() - startTime);
    }

    @Test
    @Ignore
    public void testScrollScanSearch() throws IOException {
        // 1
        String json = "{\"explain\":false,\"from\":0,\"size\":2,\"query\":{\"range\":{\"tradeAmount\":{\"gte\":10,\"lte\":2000}}}}";
        String result = query("GET", json, "/weike/member/_search?search_type=scan&scroll=1m", "s0");

        // scroll
        Map<String, String> rj = Gson.fromJson(result, HashMap.class);
        String scrollId = rj.get("_scroll_id");
        json = "{\"scroll_id\":\"" + scrollId + "\"}";
        result = query("GET", json, "/_search/scroll?scroll=1m", "s1-" + scrollId);

        // scroll
        rj = Gson.fromJson(result, HashMap.class);
        scrollId = rj.get("_scroll_id");
        json = "{\"scroll_id\":\"" + scrollId + "\"}";
        result = query("GET", json, "/_search/scroll?scroll=1m", "s2-" + scrollId);

        // clear
        rj = Gson.fromJson(result, HashMap.class);
        scrollId = rj.get("_scroll_id");
        json = "{\"scroll_id\":[\"" + scrollId + "\"]}";
        query("DELETE", json, "/_search/scroll", "c-" + scrollId);
    }
}
