/**
 * 
 */
package jframe.pushy;

import java.util.Date;

import jframe.core.plugin.annotation.Service;

/**
 * <p>
 * TODO
 * <li>处理丢失包</li>
 * <li>处理丢失连接</li>
 * </p>
 * 
 * @author dzh
 * @date Sep 30, 2014 9:03:42 AM
 * @since 1.0
 */
@Service(clazz = "jframe.pushy.impl.PushyServiceImpl", id = "jframe.service.pushy")
public interface PushyService {

	void sendMessage(String token, String payload) throws Exception;

	void sendMessage(String token, String payload, Date expirationDate)
			throws Exception;

//	List<String> getExpiredTokens(long timeout, TimeUnit timeoutUnit);
}
