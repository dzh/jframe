/**
 * 
 */
package jframe.pay.client.service;

import java.util.Map;

import jframe.core.plugin.annotation.Service;
import jframe.pay.domain.util.IDUtil;

/**
 * @author dzh
 * @date Aug 31, 2015 5:27:54 PM
 * @since 1.0
 */
@Service(clazz = "jframe.pay.client.service.HttpPayClientService", id = "jframe.pay.service.payclient")
public interface PayClientService {

	Map<String, Object> pay(Map<String, String> req) throws Exception;

	Map<String, Object> usr(Map<String, String> req) throws Exception;

	default String genPayGroup() {
		return IDUtil.genPayGroupNo();
	}

	default String genPayNo() {
		return IDUtil.genPayNo();
	}
}
