/**
 * 
 */
package jframe.yunsms.service;

import jframe.core.plugin.annotation.Service;

/**
 * manual {@link http://www.yunsms.cn/smsapi.html#zy}
 * 
 * @author dzh
 * @date Jul 9, 2016 3:35:47 PM
 * @since 1.0
 */
@Service(clazz = "jframe.yunsms.service.impl.YunsmsServiceImpl", id = "jframe.service.yunsms")
public interface YunsmsService {

    boolean send(String id, String mobile, String content);

}
