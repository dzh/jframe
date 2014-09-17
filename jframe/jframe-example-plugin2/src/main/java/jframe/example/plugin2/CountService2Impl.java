/**
 * 
 */
package jframe.example.plugin2;

import jframe.core.plugin.Plugin;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 16, 2014 5:08:19 PM
 * @since 1.0
 */
public class CountService2Impl implements CountService2 {

	static Logger LOG = LoggerFactory.getLogger(CountService2Impl.class);

	@InjectPlugin
	static Plugin plugin;

	@Start
	void start() {
		LOG.info("CountService2Impl start");
		LOG.info("Inject ExamplePlugin2 {}", plugin.getName());
	}

	public int mul(int x, int y) {
		return x * y;
	}

}
