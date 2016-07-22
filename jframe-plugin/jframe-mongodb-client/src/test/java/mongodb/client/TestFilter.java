/**
 * 
 */
package mongodb.client;

import org.bson.conversions.Bson;
import org.junit.Test;

import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;

/**
 * @author dzh
 * @date Jul 11, 2016 10:22:21 AM
 * @since 1.0
 */
public class TestFilter {

    @Test
    public void test() {
        Bson b = Filters.and(Filters.eq("n1", "v1"), Filters.lt("n2", "v2"));
        System.out.println(JSON.serialize(b));
    }

}
