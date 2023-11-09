package jframe.ext.dispatch.ons;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date 2020/1/11 19:29
 */
public class OnsProducerDispatcher extends OnsDispatcher {

    public OnsProducerDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    protected boolean enableConsumer() {
        return false;
    }
}
