package jframe.example.plugin;

import jframe.core.plugin.Plugin;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author dzh
 * @date Sep 15, 2014 3:36:29 PM
 * @since 1.0
 */
public class CountServiceImpl implements CountService {

	static Logger LOG = LoggerFactory.getLogger(CountServiceImpl.class);

	@InjectPlugin
	static Plugin plugin;

	public int add(int x, int y) {
		return x + y;
	}

	@Start
	void start() {
		LOG.info("CountServiceImpl start");
		LOG.info("ExamplePlugin {}", plugin.getName());
	}

	@Stop
	void stop() {
		LOG.info("CountServiceImpl stop");
	}

}
