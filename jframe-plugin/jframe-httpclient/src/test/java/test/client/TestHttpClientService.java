/**
 * 
 */
package test.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import org.junit.Test;

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

    @Test
    public void testEncode() throws UnsupportedEncodingException {
        String url = "localhost/test?a=2016-07-08&b=11";
        url = "http://testfirmware.blob.core.chinacloudapi.cn/channeltag/D453A67FF91F425A85AD6025CECE0685.gz?sp=rwdl&sr=c&sv=2014-02-14&se=2021-07-18T08%3A56%3A32Z&st=2016-07-18T08%3A56%3A32Z&sig=TnMMnXQvf0RnEQ53PcWk1qFfle8Erv2QO%2FAhobO%2BPQQ%3D";
        System.out.println(url.length());
        System.out.println(URLEncoder.encode(url, "utf-8"));
    }

}
