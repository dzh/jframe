/**
 * 
 */
package es;

import java.util.Date;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzh
 * @date Sep 24, 2016 5:55:15 PM
 * @since 1.0
 */
@Ignore
public class TestIndexApi {

    @Test
    public void testBuildJson() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().field("user", "kimchy").field("postDate", new Date())
                .field("message", "trying out Elasticsearch").endObject();
        String json = builder.string();
        System.out.println(json);
        int a = 1000000000;
        System.out.println(a);
    }

}
