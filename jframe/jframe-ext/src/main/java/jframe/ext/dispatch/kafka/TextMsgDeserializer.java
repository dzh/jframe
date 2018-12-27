package jframe.ext.dispatch.kafka;

import java.io.UnsupportedEncodingException;

import org.apache.kafka.common.errors.SerializationException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jframe.core.msg.Msg;
import jframe.core.msg.TextMsg;

/**
 * @author dzh
 * @date Dec 27, 2018 12:33:26 PM
 * @version 0.0.1
 */
public class TextMsgDeserializer extends MsgDeserializer<String> {

    static final Gson GSON = new GsonBuilder().serializeNulls().create();

    @Override
    public Msg<String> deserialize(String topic, byte[] data) {
        if (data == null) return null;
        try {
            return GSON.fromJson(new String(data, encoding), TextMsg.class);
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("Error when deserializing byte[] to string due to unsupported encoding " + encoding);
        }
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
