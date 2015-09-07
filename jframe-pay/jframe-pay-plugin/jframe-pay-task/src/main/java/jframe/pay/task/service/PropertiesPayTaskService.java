/**
 * 
 */
package jframe.pay.task.service;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.pay.task.PayTaskPlugin;

/**
 * @author dzh
 * @date Sep 6, 2015 3:25:39 PM
 * @since 1.0
 */
@Injector
public class PropertiesPayTaskService implements PayTaskService {

	@InjectPlugin
	static PayTaskPlugin Plugin;

	static String File_Pay = "file.pay";

	@Start
	void start() {

	}

	@Stop
	void stop() {
		
	}

}
