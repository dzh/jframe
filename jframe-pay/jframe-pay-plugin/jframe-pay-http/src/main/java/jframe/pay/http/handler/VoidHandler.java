/**
 * 
 */
package jframe.pay.http.handler;

import io.netty.channel.ChannelHandler.Sharable;

import java.util.Collections;
import java.util.Map;

import jframe.pay.domain.http.RspCode;

/**
 * @author dzh
 * @date Sep 1, 2015 7:54:16 PM
 * @since 1.0
 */
@Sharable
public class VoidHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see lech.rent.http.handler.AbstractHandler#service(java.util.Map,
	 * java.util.Map)
	 */
	@Override
	public void service(Map<String, String> req, Map<String, Object> rsp)
			throws PayException {
		RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_PATH);
	}

	protected Map<String, String> parseHttpReq(String content) {
		return Collections.emptyMap();
	}

}
