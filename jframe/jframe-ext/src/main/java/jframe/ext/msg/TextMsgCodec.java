package jframe.ext.msg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jframe.core.msg.Msg;
import jframe.core.msg.TextMsg;
import jframe.ext.msg.MsgCodec;

import java.io.IOException;

/**
 * @author dzh
 * @date 2019/12/25 21:13
 */
public class TextMsgCodec implements MsgCodec {

    protected String encoding = "UTF8";

    static final Gson GSON = new GsonBuilder().serializeNulls().create();

    @Override
    public byte[] encode(Msg<?> msg) throws IOException {
        return GSON.toJson(msg).getBytes(encoding); //todo msg's data must be String
    }

    @Override
    public Msg<String> decode(byte[] msg) throws IOException {
        return GSON.fromJson(new String(msg, encoding), TextMsg.class);
    }
}
