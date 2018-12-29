package jframe.ext.dispatch.kafka;

import jframe.core.conf.Config;

/**
 * @author dzh
 * @date Dec 27, 2018 7:48:56 PM
 * @version 0.0.1
 */
public class KafkaProducerDispatcher extends KafkaDispatcher {

    public KafkaProducerDispatcher(String id, Config config) {
        super(id, config);
    }

    @Override
    protected boolean enableConsumer() {
        return false;
    }

}
