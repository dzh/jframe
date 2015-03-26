/**
 * 
 */
package jframe.getui.andriod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.getui.GetuiConf;
import jframe.getui.GetuiPlugin;
import jframe.getui.GetuiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;

/**
 * @author dzh
 * @date Sep 29, 2014 12:01:49 PM
 * @since 1.0
 */
@Injector
public class GetuiServiceImpl implements GetuiService {

	static Logger LOG = LoggerFactory.getLogger(GetuiServiceImpl.class);

	@InjectPlugin
	static GetuiPlugin plugin;

	static String FILE_CONF = "file.getui";

	private IGtPush push;

	@Start
	void start() {
		String conf = plugin.getConfig(FILE_CONF);
		if (!new File(conf).exists()) {
			LOG.error("Not found getui config file {}", conf);
			return;
		}

		try {
			GetuiConf.init(conf);
			System.setProperty("gexin.rp.sdk.http.connection.timeout",
					GetuiConf.HTTP_CONN_TIMEOUT);
			System.setProperty("gexin.rp.sdk.http.connection.pool.size",
					GetuiConf.HTTP_CONN_COUNT);
			push = new IGtPush(GetuiConf.HOST, GetuiConf.APPKEY,
					GetuiConf.MASTER_SECRET, true);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return;
		}
		LOG.info("GetuiService start successfully!");
	}

	@Stop
	public void stop() {
		try {
			push.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		plugin = null;
	}

	public void setPush(IGtPush push) {
		this.push = push;
	}

	/**
	 * 
	 */
	@Override
	public IPushResult push2List(ITemplate data, boolean isOffline,
			long offlineExpireTime, List<String> token) {
		ListMessage message = new ListMessage();
		message.setData(data);
		message.setOffline(isOffline);
		message.setOfflineExpireTime(offlineExpireTime);

		List<Target> list = new ArrayList<Target>(token.size());
		Target target = null;
		for (String t : token) {
			target = new Target();
			target.setAppId(GetuiConf.APPID);
			target.setClientId(t);
			list.add(target);
		}
		return push.pushMessageToList(push.getContentId(message), list);
	}

	@Override
	public IPushResult push2Single(ITemplate data, boolean isOffline,
			long offlineExpireTime, String token) throws Exception {
		SingleMessage message = new SingleMessage();
		message.setOffline(isOffline);
		message.setOfflineExpireTime(offlineExpireTime);
		message.setData(data);

		Target target = new Target();
		target.setAppId(GetuiConf.APPID);
		target.setClientId(token);

		return push.pushMessageToSingle(message, target);
	}

	// public IPushResult push2App(ITemplate data, boolean isOffline,
	// long offlineExpireTime) throws Exception {
	//
	// return null;
	// }
}
