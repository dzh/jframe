/**
 * 
 */
package jframe.launcher.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @author dzh
 * @date Feb 16, 2016 4:56:45 PM
 * @since 1.0
 */
public class TestVarProperties {

    @Test
    public void testVarProperties() throws Exception {
        System.setProperty("app.home", "/home/dzh");

        VarProperties var = new VarProperties(System.getProperties());
        var.load(TestVarProperties.class.getResourceAsStream("config.properties"));
        System.out.println(var.toString());
    }

    @Test
    public void testPattern() throws Exception {
        Pattern P_VAR = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher m = P_VAR.matcher("${app.update}/${app.pid}");

        String var = null;
        while (m.find()) {
            var = m.group(1);
            System.out.println(var.toString());
        }

    }
}
