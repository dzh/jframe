/**
 * 
 */
package jframe.pay.upmp.service;

import java.util.Map;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Nov 24, 2015 5:24:58 PM
 * @since 1.0
 */
@Service(clazz = "jframe.pay.upmp.service.UpmppayServiceImpl", id = "jframe.pay.service.upmppay")
public interface UpmppayService {

    void pay(Map<String, String> req, Map<String, Object> rsp) throws Exception;

    void payBack(Map<String, String> req, Map<String, Object> rsp) throws Exception;

}
