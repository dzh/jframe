/**
 * 
 */
package test;

import org.junit.Test;

/**
 * @author dzh
 * @date Aug 3, 2016 8:01:29 PM
 * @since 1.0
 */
public class TestOS {

    @Test
    public void testOS() {
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("os.arch"));
    }

}
