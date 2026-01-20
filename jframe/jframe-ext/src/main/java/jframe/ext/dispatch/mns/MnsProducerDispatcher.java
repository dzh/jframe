package jframe.ext.dispatch.mns;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date 2022/11/17 19:20
 */
public class MnsProducerDispatcher extends MnsDispatcher {
    
    public MnsProducerDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    protected boolean enableConsumer() {
        return false;
    }
}
