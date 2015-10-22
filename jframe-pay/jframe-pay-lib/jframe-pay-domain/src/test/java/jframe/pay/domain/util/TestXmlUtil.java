/**
 * 
 */
package jframe.pay.domain.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author dzh
 * @date Oct 21, 2015 5:26:53 PM
 * @since 1.0
 */
public class TestXmlUtil {

    @Test
    public void testToXml() {
        Map<String, String> body = new HashMap<>(2,1);
        body.put("return_code", "<![CDATA[SUCCESS]]>");
        body.put("return_msg", "<![CDATA[OK]]>");

        System.out.println(XmlUtil.toXml("xml", body));
    }

}
