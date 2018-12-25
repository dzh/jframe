package jframe.qcloud.service;

import java.util.Map;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Aug 5, 2018 1:00:59 AM
 * @version 0.0.1
 */
@Service(clazz = "jframe.qcloud.service.QCloudServiceImpl", id = "jframe.service.qcloud")
public interface QCloudService {

    Map<String, Object> getFederationToken(String id);

}
