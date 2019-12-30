package jframe.ext.dispatch.rocketmq;

import jframe.core.msg.Msg;

import java.io.IOException;

/**
 * ThreadSafe
 *
 * @author dzh
 * @date 2019/12/25 21:11
 */
public interface MsgCodec {

    byte[] encode(Msg<?> msg) throws IOException;

    <T> Msg<T> decode(byte[] msg) throws IOException;

}
