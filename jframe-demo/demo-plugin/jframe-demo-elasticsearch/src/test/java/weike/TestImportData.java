/**
 * 
 */
package weike;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzh
 * @date Sep 29, 2016 2:45:58 PM
 * @since 1.0
 */
// @Ignore
public class TestImportData {

    RestClient client;
    
    @Test
    public void testFile(){
        File f = new File("dzh.txt");
        System.out.println(f.getAbsolutePath());
    }

    @Before
    public void init() {
        client = RestClient
                .builder(new HttpHost("121.199.167.226", 30002, "http"), new HttpHost("121.199.167.226", 30002, "http"))
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
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
                // }
                // })
                .build();
    }

    @Test
    @Ignore
    public void testReq() throws Exception {
        // Response response = client.performRequest("GET", "/",
        // Collections.singletonMap("pretty", "true"));
        // System.out.println(EntityUtils.toString(response.getEntity()));

        // index a document
        HttpEntity entity = new NStringEntity(
                "{\"sellerId\":1," + "\"user\" : \"kimchy\"," + "\"post_date\" : \"2009-11-15T14:12:12\","
                        + "    \"message\" : \"trying out Elasticsearch\",\"mobile\":\"18616020611\"" + "}",
                ContentType.APPLICATION_JSON);
        Response indexResponse = client.performRequest("POST", "/weike/member/", Collections.<String, String>emptyMap(),
                entity);

    }

    @Test
    public void testMd5() {
        System.out.println(Md5Util.md5Str("你好吗"));

        Set<String> list = new HashSet<String>();
        if (list.contains(null)) {
            System.out.println("c nil 1");
        }
        list.add(null);
        if (list.contains(null)) {
            System.out.println("c nil 2");
        }
        list.add(null);
        if (list.contains(null)) {
            System.out.println("c nil 3");
        }
        list.remove(null);
        if (list.contains(null)) {
            System.out.println("c nil 4");
        }
        list.remove(null);
        if (list.contains(null)) {
            System.out.println("c nil 5");
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

}
