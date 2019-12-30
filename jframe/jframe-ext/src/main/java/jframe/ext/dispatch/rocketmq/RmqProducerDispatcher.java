package jframe.ext.dispatch.rocketmq;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date 2019/12/25 18:16
 */
public class RmqProducerDispatcher extends RmqDispatcher {

    public RmqProducerDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    protected boolean enableConsumer() {
        return false;
    }
}
