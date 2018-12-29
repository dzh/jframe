package jframe.ext.dispatch.kafka;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date Dec 27, 2018 7:49:38 PM
 * @version 0.0.1
 */
public class KafkaConsumerDispatcher extends KafkaDispatcher {

    public KafkaConsumerDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    protected boolean enableProducer() {
        return false;
    }

}
