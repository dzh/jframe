package jframe.zk.service;

import org.apache.curator.framework.CuratorFramework;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Dec 12, 2018 7:12:56 PM
 * @version 0.0.1
 */
@Service(clazz = "jframe.zk.service.impl.CuratorServiceImpl", id = "jframe.service.zk.curator")
public interface CuratorService {

    CuratorFramework client(String id);

}
