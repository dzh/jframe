/**
 * 
 */
package jframe.demo.elasticsearch.weike;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.demo.elasticsearch.JfDemoESPlugin;

/**
 * @author dzh
 * @date Sep 29, 2016 8:02:45 PM
 * @since 1.0
 */
@Injector
@Path("weike")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WeikePath {

    static Logger LOG = LoggerFactory.getLogger(WeikePath.class);

    @InjectPlugin
    static JfDemoESPlugin Plugin;

    /**
     * 
     * @param req
     * @return
     */
    @SuppressWarnings("rawtypes")
    @POST
    @Path("addmember")
    public Map<String, Object> addMember(Map<String, Object> req) {
        // LOG.debug("addMember req-{}", req);
        Map<String, Object> rsp = new HashMap<>();

        Object sellerId = req.getOrDefault("sellerId", "");
        Object memList = req.get("members");

        if ("".equals(sellerId)) {
            rsp.putIfAbsent("rspCode", 1);
        }
        if (memList == null) {
            rsp.putIfAbsent("rspCode", 2);
        }

        if (memList != null) {
            if (memList instanceof List) {
                // for (Object mem : (List) memList) {
                // try {
                // indexMember(sellerId.toString(), mem);
                // } catch (Exception e) {
                // LOG.error(e.getMessage(), e.fillInStackTrace());
                // rsp.putIfAbsent("rspCode", -1);
                // rsp.put("rspDesc", e.getMessage());
                // }
                // }
                try {
                    bulkIndexMember((List) memList);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e.fillInStackTrace());
                    rsp.putIfAbsent("rspCode", -1);
                    rsp.put("rspDesc", e.getMessage());
                }
            } else {
                // indexMember(memList);
            }
        }
        rsp.putIfAbsent("rspCode", 0);
        return rsp;
    }

    private void bulkIndexMember(List<?> memList) throws Exception {
        StringBuilder buf = new StringBuilder(1024);
        for (Object mem : memList) {
            buf.append("{\"index\": {}}");
            buf.append("\n");
            buf.append(Gson.toJson(mem));
            buf.append("\n");
        }

        long startTime = System.currentTimeMillis();
        RestClient client = Plugin.client;

        HttpEntity entity = new NStringEntity(buf.toString(), ContentType.APPLICATION_JSON);

        Response indexResponse = client.performRequest("POST", "/weike/member/_bulk",
                Collections.<String, String>emptyMap(), entity);

        if (LOG.isDebugEnabled()) {
            LOG.debug("indexMember {}ms", System.currentTimeMillis() - startTime);
            LOG.debug("indexResponse {}", indexResponse.toString());
        }
    }

    static Gson Gson = new Gson();

    private void indexMember(String sellerId, Object mem) throws IOException {
        if (sellerId == null)
            sellerId = "";

        long startTime = System.currentTimeMillis();

        RestClient client = Plugin.client;
        String json = Gson.toJson(mem);

        HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
        String path = "/weike/member";
        if (!"".equals(sellerId)) {
            path += "?routing=" + sellerId;
        }
        Response indexResponse = client.performRequest("POST", path, Collections.<String, String>emptyMap(), entity);

        if (LOG.isDebugEnabled()) {
            LOG.debug("indexMember {}ms", System.currentTimeMillis() - startTime);
            LOG.debug("indexResponse {}", indexResponse.toString());
        }
    }

}
