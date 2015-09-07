/**
 * 
 */
package jframe.pay.http.usr.service;

import java.util.Map;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.pay.dao.service.PayDaoService;
import jframe.pay.domain.Fields;
import jframe.pay.domain.util.ObjectUtil;

/**
 * @author dzh
 * @date Sep 2, 2015 1:59:32 AM
 * @since 1.0
 */
@Injector
public class CommonService implements Fields {

	@InjectService(id = "jframe.pay.service.dao")
	protected static PayDaoService PayDao;

	public <K, V> void setIfEmpty(Map<K, V> map, K key, V defVal) {
		if (ObjectUtil.isEmpty(map.get(key))) {
			map.put(key, defVal);
		}
	}

}
