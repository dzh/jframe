/**
 * 
 */
package jframe.ext.dispatch.activemq;

import com.google.gson.Gson;

import jframe.core.msg.Msg;
import jframe.core.msg.TextMsg;

/**
 * @author dzh
 * @date Oct 17, 2014 3:32:17 PM
 * @since 1.0
 */
public class TextMsgTransfer implements MsgTransfer {

	Gson gson = new Gson();

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.ext.dispatch.MsgTransfer#encode(jframe.core.msg.Msg)
	 */
	public String encode(Msg<?> msg) {
		return gson.toJson(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.ext.dispatch.MsgTransfer#decode(java.lang.String)
	 */
	public Msg<?> decode(String msg) {
		return gson.fromJson(msg, TextMsg.class);
	}

}
