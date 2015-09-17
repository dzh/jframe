package jframe.pay.wx.http;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ClientRequestHandler extends PrepayIdRequestHandler {

	public ClientRequestHandler() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public String getXmlBody() {
		StringBuilder sb = new StringBuilder();
		Set es = super.getAllParameters().entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (!"appkey".equals(k)) {
				sb.append("<" + k + ">" + v + "<" + k + ">" + "\r\n");
			}
		}
		return sb.toString();
	}
}
