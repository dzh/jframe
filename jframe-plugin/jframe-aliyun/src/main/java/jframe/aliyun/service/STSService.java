/**
 * 
 */
package jframe.aliyun.service;

import java.util.Map;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Feb 28, 2016 9:13:38 AM
 * @since 1.0
 */
@Service(clazz = "jframe.aliyun.service.sts.STSServiceImpl", id = "jframe.aliyun.service.sts")
public interface STSService {

    /**
     * 获取临时访问权限
     * 
     * @param id
     * @return
     */
    Map<String, String> getTempAccessPerm(String id);

}
