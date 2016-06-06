/**
 * 
 */
package jframe.zk.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.zk.service.ZkService;

/**
 * @author dzh
 * @date May 4, 2016 5:35:58 PM
 * @since 1.0
 */
@Injector
public class ZkServiceImpl implements ZkService {

    static Logger LOG = LoggerFactory.getLogger(ZkServiceImpl.class);

    @Start
    void start() {

    }

    /**
     * 
     * @param file
     *            zkcli.properties
     */
    public static void start(String file) {
        // ZooKeeper zk = new Zookeeper();
    }

    @Stop
    void stop() {

    }

}
