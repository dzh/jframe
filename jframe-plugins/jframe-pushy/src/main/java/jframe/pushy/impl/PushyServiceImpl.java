/**
 * 
 */
package jframe.pushy.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.pushy.PushyConf;
import jframe.pushy.PushyPlugin;
import jframe.pushy.PushyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.relayrides.pushy.apns.ApnsEnvironment;
import com.relayrides.pushy.apns.ExpiredToken;
import com.relayrides.pushy.apns.FeedbackConnectionException;
import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.PushManagerFactory;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;

/**
 * @author dzh
 * @date Sep 30, 2014 9:04:30 AM
 * @since 1.0
 */
@Injector
class PushyServiceImpl implements PushyService {

	static Logger LOG = LoggerFactory.getLogger(PushyServiceImpl.class);

	@InjectPlugin
	static PushyPlugin plugin;

	static String FILE_CONF = "file.pushy";

	PushManager<SimpleApnsPushNotification> pushManager;

	@Start
	void start() {
		String conf = plugin.getConfig(FILE_CONF);
		if (!new File(conf).exists()) {
			LOG.error("Not found pushy config file {}", conf);
			return;
		}

		try {
			PushyConf.init(conf);
			PushManagerFactory<SimpleApnsPushNotification> pushManagerFactory = new PushManagerFactory<SimpleApnsPushNotification>(
					getEnvironment(PushyConf.HOST, PushyConf.HOST_PORT,
							PushyConf.FEEDBACK, PushyConf.FEEDBACK_PORT),
					PushManagerFactory.createDefaultSSLContext(
							plugin.getConfig(Config.APP_CONF) + "/"
									+ PushyConf.IOS_AUTH,
							PushyConf.IOS_PASSWORD));

			pushManager = pushManagerFactory.buildPushManager();
			pushManager.start();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return;
		}
		LOG.info("PushyService start successfully!");

		// pushManager.registerRejectedNotificationListener(listener);
		// pushManager.registerFailedConnectionListener(listener)
	}

	public static ApnsEnvironment getEnvironment(String host, String port,
			String feedback, String fdPort) {
		return new ApnsEnvironment(host, Integer.parseInt(port), feedback,
				Integer.parseInt(fdPort));
	}

	@Stop
	void stop() {
		if (pushManager != null) {
			try {
				pushManager.shutdown(10000);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
		}
		plugin = null;
	}

	@Override
	public void sendMessage(String token, String payload) {
		sendMessage(token, payload, null);
	}

	@Override
	public List<String> getExpiredTokens(long timeout, TimeUnit timeoutUnit) {
		List<ExpiredToken> list = null;
		try {
			list = pushManager.getExpiredTokens(10, TimeUnit.SECONDS);
		} catch (InterruptedException | FeedbackConnectionException e) {
			LOG.error(e.getMessage());
		}
		if (list == null) {
			list = Collections.emptyList();
		}

		List<String> tokens = new ArrayList<String>(list.size());
		for (ExpiredToken t : list) {
			tokens.add(TokenUtil.tokenBytesToString(t.getToken()));
		}
		return tokens;
	}

	@Override
	public void sendMessage(String token, String payload, Date expirationDate) {
		try {
			pushManager.getQueue().put(
					new SimpleApnsPushNotification(TokenUtil
							.tokenStringToByteArray(token), payload,
							expirationDate));
		} catch (InterruptedException e) {
			LOG.warn(e.getMessage());
		}
	}

}
