/**
 * 
 */
package jframe.mongodb.client.service.impl;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.mongodb.client.MongoClientPlugin;
import jframe.mongodb.client.service.MongoClientService;

/**
 * @author dzh
 * @date Jul 6, 2015 3:45:04 PM
 * @since 1.0
 */
@Injector
public class MongoClientServiceImpl implements MongoClientService {

	@InjectPlugin
	static MongoClientPlugin plugin;
	

	@Start
	void start() {

	}
	
	

	@Stop
	void stop() {

	}

}
