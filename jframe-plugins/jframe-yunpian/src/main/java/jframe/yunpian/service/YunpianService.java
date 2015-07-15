/**
 * 
 */
package jframe.yunpian.service;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Jul 14, 2015 6:00:30 PM
 * @since 1.0
 */
@Service(clazz = "jframe.yunpian.service.YunpianServiceImpl", id = "jframe.service.yunpian")
public interface YunpianService {

	void send(String text, String extend, String uid, String callback,
			String... mobile);

	void send(String text, String... mobile);

}
