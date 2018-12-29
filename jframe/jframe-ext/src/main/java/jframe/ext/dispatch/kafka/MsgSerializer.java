package jframe.ext.dispatch.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import jframe.core.msg.Msg;

/**
 * @author dzh
 * @date Dec 27, 2018 12:17:10 PM
 * @version 0.0.1
 */
public abstract class MsgSerializer<T> implements Serializer<Msg<T>> {
    protected String encoding = "UTF8";

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) encodingValue = configs.get("serializer.encoding");

        if (encodingValue instanceof String) encoding = (String) encodingValue;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
