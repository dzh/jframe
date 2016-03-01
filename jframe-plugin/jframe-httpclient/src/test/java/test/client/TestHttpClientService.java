/**
 * 
 */
package test.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author dzh
 * @date Feb 12, 2015 9:36:36 AM
 * @since 1.0
 */
public class TestHttpClientService {

    public void testException() {
        try {
            throwExcep();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void throwExcep() throws Exception {
        try {
            throw new Exception("xxx");
        } finally {
            System.out.println("throw E");
        }
    }

    static final Gson GSON = new GsonBuilder().create();

    public void testJson() {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpHost target = new HttpHost("120.27.182.142", 80, HttpHost.DEFAULT_SCHEME_NAME);
            HttpRequestBase request = new HttpPost(target.toURI() + "/mry/usr/qryusr");
            request.addHeader("Api-Token", "76067");

            String data = "";
            // ((HttpPost) request).setEntity(new StringEntity(data,
            // ContentType.create("text/plain", "utf-8")));

            CloseableHttpResponse resp = httpClient.execute(request);
            HttpEntity entity = resp.getEntity();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = entity.getContent();
            byte[] buf = new byte[32];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            String str = new String(baos.toByteArray(), "utf-8");
            for (byte b : baos.toByteArray()) {
                System.out.print(b);
                System.out.print(' ');
            }

            Reader reader = new InputStreamReader(entity.getContent(), ContentType.getOrDefault(entity).getCharset());
            // TODO decode by mime-type

            Map<String, String> rspMap = GSON.fromJson(reader, HashMap.class);
            String usrJson = rspMap.get("usr");
            Map<String, Object> usr = GSON.fromJson(usrJson, HashMap.class);
            System.out.println(usr.get("id").toString() + usr.get("name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
