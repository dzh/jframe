package jframe.ext.dispatch.ons;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date 2020/1/11 19:26
 */
public class OnsConsumerDispatcher extends OnsDispatcher {

    public OnsConsumerDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    protected boolean enableProducer() {
        return false;
    }

}
