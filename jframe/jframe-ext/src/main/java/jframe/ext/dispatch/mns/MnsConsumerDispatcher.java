package jframe.ext.dispatch.mns;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date 2022/11/17 19:20
 */
public class MnsConsumerDispatcher extends MnsDispatcher {

    public MnsConsumerDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    protected boolean enableProducer() {
        return false;
    }
}
