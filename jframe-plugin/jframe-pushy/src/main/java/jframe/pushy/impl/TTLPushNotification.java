/**
 * 
 */
package jframe.pushy.impl;

import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;

/**
 * @author dzh
 * @date Sep 30, 2014 11:38:05 AM
 * @since 1.0
 */
public class TTLPushNotification extends SimpleApnsPushNotification {

	public TTLPushNotification(byte[] token, String payload) {
		super(token, payload);
	}

}
