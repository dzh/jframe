/**
 * 
 */
package jframe.httpclient.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author dzh
 * @date Dec 3, 2014 10:33:53 AM
 * @since 1.0
 */
@Immutable
public class HttpUtil extends URLEncodedUtils {

	/**
	 * 
	 * @param paras
	 * @param charset
	 * @return @return An {@code application/x-www-form-urlencoded} string
	 */
	public final static String format(Map<String, String> paras, String charset) {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>(paras
				.keySet().size());
		for (Map.Entry<String, String> param : paras.entrySet()) {
			NameValuePair pair = new BasicNameValuePair(param.getKey(),
					param.getValue());
			paramList.add(pair);
		}
		return format(paramList, charset);
	}
}
