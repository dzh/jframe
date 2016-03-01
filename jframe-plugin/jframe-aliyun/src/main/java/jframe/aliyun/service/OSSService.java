/**
 * 
 */
package jframe.aliyun.service;

import com.aliyun.oss.OSSClient;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Feb 22, 2016 11:07:15 AM
 * @since 1.0
 */
@Service(clazz = "jframe.aliyun.service.oss.OSSServiceImpl", id = "jframe.service.aliyun.oss")
public interface OSSService {

    OSSClient getOSSClient(String id);

}
