/**
 * 
 */
package jframe.pay.wx.http.client;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dzh
 * @date Oct 17, 2015 9:51:40 AM
 * @since 1.0
 */
public class TestWxServiceNew {

    @Test
    public void testXml() throws Exception {
        String content = "<xml> <return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]>"
                + "</return_msg><appid><![CDATA[wx8fd8bd28cea002fd]]></appid><mch_id><![CDATA[1252672001]]></mch_id>"
                + "<nonce_str><![CDATA[dYsIEeHnqdmc8G1O]]></nonce_str><sign><![CDATA[74D85CEBC1EF6D67F7273061DFB9CB9C]]></sign>"
                + "<result_code><![CDATA[SUCCESS]]></result_code><prepay_id><![CDATA[wx2015101617451557b7fb34c80052355013]]>"
                + "</prepay_id><trade_type><![CDATA[APP]]></trade_type></xml>";
        Map<String, String> map = WxServiceNew.decodeXml(content);
        Assert.assertEquals("SUCCESS", map.get("return_code"));
    }

}
