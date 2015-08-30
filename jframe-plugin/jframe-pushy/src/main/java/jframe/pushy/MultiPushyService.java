/**
 * 
 */
package jframe.pushy;

import java.util.Date;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Aug 29, 2015 2:03:34 PM
 * @since 1.0
 */
@Service(clazz = "jframe.pushy.impl.MultiPushyServiceImpl", id = "jframe.service.multipushy")
public interface MultiPushyService {

	void sendMessage(String id, String token, String payload) throws Exception;

	void sendMessage(String id, String token, String payload,
			Date expirationDate) throws Exception;
}
