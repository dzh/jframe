/**
 * 
 */
package jframe.getui.andriod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.getui.ConfField;
import jframe.getui.GetuiPlugin;
import jframe.getui.MultiGetuiConf;
import jframe.getui.MultiGetuiService;

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
 * @date Aug 20, 2015 3:53:49 PM
 * @since 1.0
 */
@Injector
public class MultiGetuiServiceImpl implements MultiGetuiService {

	static Logger LOG = LoggerFactory.getLogger(GetuiServiceImpl.class);

	@InjectPlugin
	static GetuiPlugin plugin;

	static String FILE_CONF = "file.getui";

	private MultiGetuiConf mgc = null;

	private Map<String, IGtPush> pushMap;

	@Start
	void start() {
		String conf = plugin.getConfig(FILE_CONF);
		start(conf);
	}

	private void start(String conf) {
		if (!new File(conf).exists()) {
			LOG.error("Not found getui config file {}", conf);
			return;
		}

		try {
			start(new FileInputStream(conf));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	private void start(InputStream is) throws Exception {
		mgc = new MultiGetuiConf();
		mgc.init(is);

		String[] ids = mgc.getGroupIds();
		pushMap = new HashMap<String, IGtPush>(ids.length, 1);
		for (String id : ids) {
			System.setProperty("gexin.rp.sdk.http.connection.timeout",
					mgc.getConf(id, ConfField.KEY_HTTP_CONN_TIMEOUT, "10000"));
			System.setProperty("gexin.rp.sdk.http.connection.pool.size",
					mgc.getConf(id, ConfField.KEY_HTTP_CONN_COUNT, "100"));

			pushMap.put(
					id,
					new IGtPush(mgc.getConf(id, ConfField.KEY_HOST), mgc
							.getConf(id, ConfField.KEY_APPKEY), mgc.getConf(id,
							ConfField.KEY_MASTER_SECRET)));
		}

		LOG.info("GetuiService start successfully!");
	}

	public static MultiGetuiService test(InputStream is) {
		MultiGetuiServiceImpl svc = new MultiGetuiServiceImpl();
		try {
			svc.start(is);
		} catch (Exception e) {

		}

		return svc;
	}

	@Stop
	public void stop() {
		for (IGtPush p : pushMap.values()) {
			try {
				p.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
		plugin = null;
	}

	/**
	 * 
	 */
	@Override
	public IPushResult push2List(String id, ITemplate data, boolean isOffline,
			long offlineExpireTime, List<String> token) {
		IGtPush push = pushMap.get(id);

		ListMessage message = new ListMessage();
		message.setData(data);
		message.setOffline(isOffline);
		message.setOfflineExpireTime(offlineExpireTime);

		List<Target> list = new ArrayList<Target>(token.size());
		Target target = null;
		for (String t : token) {
			target = new Target();
			target.setAppId(mgc.getConf(id, ConfField.KEY_APPID));
			target.setClientId(t);
			list.add(target);
		}
		return push.pushMessageToList(push.getContentId(message), list);
	}

	@Override
	public IPushResult push2Single(String id, ITemplate data,
			boolean isOffline, long offlineExpireTime, String token)
			throws Exception {
		IGtPush push = pushMap.get(id);

		SingleMessage message = new SingleMessage();
		message.setOffline(isOffline);
		message.setOfflineExpireTime(offlineExpireTime);
		message.setData(data);

		Target target = new Target();
		target.setAppId(mgc.getConf(id, ConfField.KEY_APPID));
		target.setClientId(token);

		return push.pushMessageToSingle(message, target);
	}

	public String getConf(String id, String key, String defVal) {
		return mgc.getConf(id, key, defVal);
	}

	@Override
	public String getConf(String id, String key) {
		return mgc.getConf(id, key, "");
	}

	// public IPushResult push2App(ITemplate data, boolean isOffline,
	// long offlineExpireTime) throws Exception {
	//
	// return null;
	// }
}
