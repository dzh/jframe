/**
 * 
 */
package jframe.pushy.impl;

import com.relayrides.pushy.apns.FailedConnectionListener;
import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;

/**
 * @author dzh
 * @date Sep 30, 2014 11:31:24 AM
 * @since 1.0
 */
public class MyFailedConnectioinListener implements
		FailedConnectionListener<SimpleApnsPushNotification> {

	@Override
	public void handleFailedConnection(
			PushManager<? extends SimpleApnsPushNotification> pushManager,
			Throwable cause) {

	}

}
