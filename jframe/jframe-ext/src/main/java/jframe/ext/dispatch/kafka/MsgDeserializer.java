package jframe.ext.dispatch.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import jframe.core.msg.Msg;

/**
 * @author dzh
 * @date Dec 27, 2018 12:28:43 PM
 * @version 0.0.1
 */
public abstract class MsgDeserializer<T> implements Deserializer<Msg<T>> {
    protected String encoding = "UTF8";

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.deserializer.encoding" : "value.deserializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) encodingValue = configs.get("deserializer.encoding");

        if (encodingValue != null && encodingValue instanceof String) encoding = (String) encodingValue;
    }

    @Override
    public void close() {
        // nothing to do
    }

}
