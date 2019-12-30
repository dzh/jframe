package jframe.ext.dispatch.rocketmq;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date 2019/12/25 18:15
 */
public class RmqConsumerDispatcher extends RmqDispatcher {

    public RmqConsumerDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    protected boolean enableProducer() {
        return false;
    }
}
