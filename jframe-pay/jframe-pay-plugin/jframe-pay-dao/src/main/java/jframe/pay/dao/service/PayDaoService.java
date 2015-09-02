/**
 * 
 */
package jframe.pay.dao.service;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Sep 2, 2015 2:45:57 AM
 * @since 1.0
 */
@Service(clazz = "jframe.pay.dao.service.MysqlPayDaoService", id = "jframe.pay.service.dao")
public interface PayDaoService extends UsrDao, OrderDao, MemcachedKey {

}
