/**
 * 
 */
package jframe.pushy.impl;

import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.RejectedNotificationListener;
import com.relayrides.pushy.apns.RejectedNotificationReason;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;

/**
 * @author dzh
 * @date Sep 30, 2014 11:32:55 AM
 * @since 1.0
 */
public class MyRejectedNotificationListener implements
		RejectedNotificationListener<SimpleApnsPushNotification> {

	@Override
	public void handleRejectedNotification(
			PushManager<? extends SimpleApnsPushNotification> pushManager,
			SimpleApnsPushNotification notification,
			RejectedNotificationReason rejectionReason) {

	}

}
