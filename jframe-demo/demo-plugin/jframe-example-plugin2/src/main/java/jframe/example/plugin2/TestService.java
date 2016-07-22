/**
 * 
 */
package jframe.example.plugin2;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.example.plugin.CountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 15, 2014 5:50:53 PM
 * @since 1.0
 */
@Injector
public class TestService {

	static Logger LOG = LoggerFactory.getLogger(TestService.class);

	@InjectPlugin
	private static ExamplePlugin2 plugin;

	@InjectService(id = "jframe.example.CountService")
	private static CountService cs;

	public void test() {
		LOG.info("TestService 11 + 22 = {}", cs.add(11, 22));
		LOG.info("Inject ExamplePlugin2 {}", plugin.getName());
	}

}
