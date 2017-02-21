/**
 * 
 */
package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * @author dzh
 * @date Nov 1, 2016 1:11:25 PM
 * @since 1.0
 */
public class Test1 {

    @Test
    public void test() {

        Map<String, Object> part = new HashMap<>();
        part.put("1", "r");
        part.put("e", "x");
        part.put("x", "g");

        for (Entry<String, Object> e : part.entrySet()) {
            System.out.println(e);
        }
    }

}
