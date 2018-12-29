package jframe.ext.dispatch.kafka;

import java.io.UnsupportedEncodingException;

import org.apache.kafka.common.errors.SerializationException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jframe.core.msg.Msg;

/**
 * @author dzh
 * @date Dec 27, 2018 12:33:00 PM
 * @version 0.0.1
 */
public class TextMsgSerializer extends MsgSerializer<String> {

    static final Gson GSON = new GsonBuilder().serializeNulls().create();

    @Override
    public byte[] serialize(String topic, Msg<String> data) {
        if (data == null) return null;

        try {
            return GSON.toJson(data).getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + encoding);
        }
    }

}
