package jframe.id.service;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date 2025/6/21 16:10
 */
@Service(clazz = "jframe.id.service.SnowflakeService", id = IdService.ID)
public interface IdService {

    String ID = "jframe.service.id";

    long nextId();
}
