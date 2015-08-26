/**
 * 
 */
package jframe.activemq.service;

import jframe.activemq.ActivemqPlugin;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;

/**
 * @author dzh
 * @date Jul 31, 2015 2:49:55 PM
 * @since 1.0
 */
@Injector
public class ActivemqServiceImpl implements ActivemqService {

	@InjectPlugin
	static ActivemqPlugin Plugin;

	@Start
	void start() {
		
	}

	@Stop
	void stop() {

	}

}
