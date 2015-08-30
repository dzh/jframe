/**
 * 
 */
package jframe.pushy.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.pushy.MultiPushyConf;
import jframe.pushy.MultiPushyService;
import jframe.pushy.PushyPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;

/**
 * @author dzh
 * @date Aug 29, 2015 2:05:18 PM
 * @since 1.0
 */
public class MultiPushyServiceImpl implements MultiPushyService {

	static Logger LOG = LoggerFactory.getLogger(MultiPushyServiceImpl.class);

	@InjectPlugin
	static PushyPlugin plugin;

	static String FILE_CONF = "file.pushy";

	private Map<String, PushManagerWrapper> pushMap;

	@Start
	void start() {
		String conf = plugin.getConfig(FILE_CONF);
		if (!new File(conf).exists()) {
			LOG.error("Not found pushy config file {}", conf);
			return;
		}

		try {
			MultiPushyConf multiConf = new MultiPushyConf();
			multiConf.init(new FileInputStream(conf));

			String[] ids = multiConf.getGroupIds();
			pushMap = new HashMap<String, PushManagerWrapper>(ids.length, 1);
			for (String group : ids) {
				PushManagerWrapper pm = new PushManagerWrapper();
				pm.init(group, multiConf);
				pm.start();
				pushMap.put(group, pm);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return;
		}
		LOG.info("PushyService start successfully!");

		// pushManager.registerRejectedNotificationListener(listener);
		// pushManager.registerFailedConnectionListener(listener)
		// class MyRejectedNotificationListener implements
		// RejectedNotificationListener<SimpleApnsPushNotification> {
		//
		// @Override
		// public void handleRejectedNotification(
		// final PushManager<? extends SimpleApnsPushNotification> pushManager,
		// final SimpleApnsPushNotification notification,
		// final RejectedNotificationReason reason) {

		// System.out.format("%s was rejected with rejection reason %s\n",
		// notification, reason);
		// }
		// }
		// pushManager
		// .registerRejectedNotificationListener(new
		// MyRejectedNotificationListener());

		// class MyFailedConnectionListener implements
		// FailedConnectionListener<SimpleApnsPushNotification> {
		// @Override
		// public void handleFailedConnection(
		// final PushManager<? extends SimpleApnsPushNotification> pushManager,
		// final Throwable cause) {
		// System.out.println(cause.getMessage());
		// }
		// }
		// pushManager
		// .registerFailedConnectionListener(new MyFailedConnectionListener());
	}

	@Stop
	public void stop() {
		if (pushMap != null) {
			for (PushManagerWrapper p : pushMap.values()) {
				p.stop();
			}
		}
		plugin = null;
	}

	@Override
	public void sendMessage(String id, String token, String payload)
			throws Exception {
		sendMessage(id, token, payload, null);
	}

	@Override
	public void sendMessage(String id, String token, String payload,
			Date expirationDate) throws Exception {
		try {
			PushManagerWrapper p = pushMap.get(id);
			if (p != null)
				p.getPushManager()
						.getQueue()
						.put(new SimpleApnsPushNotification(TokenUtil
								.tokenStringToByteArray(token), payload,
								expirationDate));
		} catch (InterruptedException e) {
			LOG.warn(e.getMessage());
		}
	}

	public static void test(InputStream is) {

	}
}
