/**
 * 
 */
package jframe.pushy.impl;

import jframe.pushy.Fields;
import jframe.pushy.MultiPushyConf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.relayrides.pushy.apns.ApnsEnvironment;
import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.PushManagerConfiguration;
import com.relayrides.pushy.apns.util.SSLContextUtil;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;

/**
 * @author dzh
 * @date Aug 29, 2015 2:11:43 PM
 * @since 1.0
 */
public class PushManagerWrapper implements Fields {

	static Logger LOG = LoggerFactory.getLogger(PushManagerWrapper.class);

	PushManager<SimpleApnsPushNotification> pushManager;

	private MultiPushyConf conf;

	private String group;

	public PushManagerWrapper init(String group, MultiPushyConf conf) {
		this.conf = conf;
		this.group = group;
		return this;
	}

	public void start() {
		try {
			PushManagerConfiguration pushConf = new PushManagerConfiguration();
			int connCount = Integer.parseInt(conf.getConf(group,
					KEY_PUSH_CONN_COUNT, "100"));
			pushConf.setConcurrentConnectionCount(connCount);

			pushManager = new PushManager<SimpleApnsPushNotification>(
					getEnvironment(conf.getConf(group, KEY_HOST),
							conf.getConf(group, KEY_HOST_PORT),
							conf.getConf(group, KEY_FEEDBACK),
							conf.getConf(group, KEY_FEEDBACK_PORT)),
					SSLContextUtil.createDefaultSSLContext(
							conf.getConf(group, KEY_IOS_AUTH),
							conf.getConf(group, KEY_IOS_PASSWORD)), null, null,
					null, pushConf, "PushManager-" + group);

			pushManager.start();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	public PushManager<SimpleApnsPushNotification> getPushManager() {
		return pushManager;
	}

	public void stop() {
		try {
			if (pushManager != null) {
				pushManager.shutdown();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	public static ApnsEnvironment getEnvironment(String host, String port,
			String feedback, String fdPort) {
		return new ApnsEnvironment(host, Integer.parseInt(port), feedback,
				Integer.parseInt(fdPort));
	}

}
