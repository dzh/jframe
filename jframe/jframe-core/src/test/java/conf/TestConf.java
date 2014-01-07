/**
 * 
 */
package conf;

import jframe.core.conf.Config;
import jframe.core.conf.DefConfig;
import jframe.core.conf.VarHandler;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dzh
 * @date Oct 11, 2013 10:03:59 PM
 * @since 1.0
 */
public class TestConf {

	@Test
	public void testVarHandler() {
		Config conf = new DefConfig();
		conf.addConfig("app.home", "/home/dzh");

		VarHandler var = new VarHandler(conf);
		String input = "${app.home}/conf";
		Assert.assertEquals(true, var.hasVar(input));
		Assert.assertEquals("/home/dzh/conf", var.replace(input));
	}

}
