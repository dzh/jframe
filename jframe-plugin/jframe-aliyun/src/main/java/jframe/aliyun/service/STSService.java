/**
 *
 */
package jframe.aliyun.service;

import jframe.core.plugin.annotation.Service;

import java.util.Map;

/**
 * @author dzh
 * @date Feb 28, 2016 9:13:38 AM
 * @since 1.0
 */
@Service(clazz = "jframe.aliyun.service.sts.STSServiceImpl", id = STSService.ID)
public interface STSService {

    String ID = "jframe.service.aliyun.sts";

    /**
     * 获取临时访问权限
     *
     * @param id
     * @return
     */
    Map<String, String> getTempAccessPerm(String id);

}
