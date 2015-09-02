/**
 * 
 */
package jframe.pay.alipay.service;

import java.util.Map;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Nov 21, 2014 4:09:15 PM
 * @since 1.0
 */
@Service(clazz = "jframe.pay.alipay.service.AlipayServiceImpl", id = "jframe.pay.service.alipay")
public interface AlipayService {

	void pay(Map<String, String> req, Map<String, Object> rsp) throws Exception;

	void payBack(Map<String, String> req, Map<String, Object> rsp)
			throws Exception;

	void clientPayBack(Map<String, String> req, Map<String, Object> rsp)
			throws Exception;
}
