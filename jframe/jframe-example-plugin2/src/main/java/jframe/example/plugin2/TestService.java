/**
 * 
 */
package jframe.example.plugin2;

import jframe.core.plugin.Plugin;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.InjectService;
import jframe.example.plugin.CountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 15, 2014 5:50:53 PM
 * @since 1.0
 */
public class TestService {

	static Logger LOG = LoggerFactory.getLogger(TestService.class);

	@InjectPlugin
	private static Plugin plugin;

	@InjectService(id = "example.CountService")
	private static CountService cs;

	public void test() {
		LOG.info("TestService 11 + 22 = {}", cs.add(11, 22));
		LOG.info("Inject ExamplePlugin2 {}", plugin.getName());
	}

}
