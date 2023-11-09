package jframe.zk.service;

import jframe.core.plugin.annotation.Service;
import org.apache.curator.framework.CuratorFramework;

/**
 * @author dzh
 * @version 0.0.1
 * @date Dec 12, 2018 7:12:56 PM
 */
@Service(clazz = "jframe.zk.service.impl.CuratorServiceImpl", id = CuratorService.ID)
public interface CuratorService {

    String ID = "jframe.service.zk.curator";

    CuratorFramework client(String id);

}
