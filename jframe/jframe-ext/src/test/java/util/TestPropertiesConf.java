/**
 * 
 */
package util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jframe.core.conf.Config;
import jframe.core.conf.FrameConfig;
import jframe.core.conf.VarHandler;
import jframe.ext.util.PropertiesConfig;

/**
 * @author dzh
 * @date Nov 17, 2014 5:54:49 PM
 * @since 1.0
 */
public class TestPropertiesConf {

    private PropertiesConfig PropertiesConfig;

    @Before
    public void testPayOption() throws Exception {
        PropertiesConfig = new PropertiesConfig();
        PropertiesConfig
                .init(Thread.currentThread().getContextClassLoader().getResourceAsStream("util/conf-demo.properties"));
    }

    @Test
    public void testConf() {
        Assert.assertEquals("11", PropertiesConfig.getConf("1", "key1"));
        Assert.assertEquals("", PropertiesConfig.getConf("1", "key2"));
        Assert.assertEquals("33", PropertiesConfig.getConf("1", "key3"));
        Assert.assertEquals("22", PropertiesConfig.getConf("2", "key2"));
        Assert.assertEquals("44", PropertiesConfig.getConf("2", "key4"));
        Assert.assertEquals("33", PropertiesConfig.getConf(null, "key3"));
        Assert.assertEquals("44", PropertiesConfig.getConf(null, "key4"));

        Config conf = new FrameConfig();
        conf.setConfig("app.home", "xx");
        VarHandler vh = new VarHandler(conf);
        PropertiesConfig.replace(vh);
        Assert.assertEquals("xx/conf", PropertiesConfig.getConf(null, "key5"));
    }

}
