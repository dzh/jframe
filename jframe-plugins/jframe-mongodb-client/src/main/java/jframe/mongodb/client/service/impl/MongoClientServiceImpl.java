/**
 * 
 */
package jframe.mongodb.client.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.mongodb.client.MongoClientConf;
import jframe.mongodb.client.MongoClientPlugin;
import jframe.mongodb.client.service.MongoClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * @author dzh
 * @date Jul 6, 2015 3:45:04 PM
 * @since 1.0
 */
@Injector
public class MongoClientServiceImpl implements MongoClientService {

	static Logger LOG = LoggerFactory.getLogger(MongoClientServiceImpl.class);

	@InjectPlugin
	static MongoClientPlugin Plugin;

	static String FILE_CONF = "file.mongoclient";

	static MongoClientConf MongoConf;

	Map<String, MongoClient> clients;

	@Start
	void start() {
		start(Plugin.getConfig(FILE_CONF, ""));
	}

	void start(String path) {
		File conf = new File(path);
		if (!conf.exists()) {
			LOG.error("Not found mongo-client.properties");
			return;
		}

		MongoConf = new MongoClientConf();
		try {
			MongoConf.init(new FileInputStream(conf));
			String[] ids = MongoConf.getGroupIds();
			clients = new HashMap<String, MongoClient>(ids.length, 1);
			for (String id : ids) {
				loadMongoClient(id);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return;
		}
		LOG.info("Load MongoClientService successfully!");
	}

	private void loadMongoClient(String id) {
		String uri = MongoConf.getConf(id, MongoClientConf.P_uri, "");
		if ("".equals(uri)) {
			LOG.warn("Not found {}'s uri", id);
			return;
		}

		MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));
		clients.put(id, mongoClient);
	}

	@Stop
	public void stop() {
		if (clients == null)
			return;
		for (Entry<String, MongoClient> mc : clients.entrySet()) {
			try {
				mc.getValue().close();
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
	}

	@Override
	public MongoClient getClient(String id) {
		return clients.get(id);
	}

	public static final MongoClientService test(String path) {
		MongoClientServiceImpl mcs = new MongoClientServiceImpl();
		mcs.start(path);
		return mcs;
	}
}
