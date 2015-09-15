/**
 * 
 */
package jframe.pay.domain.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author dzh
 * @date Jul 13, 2015 5:44:13 PM
 * @since 1.0
 */
public class HttpUtil {

	/**
	 * If req not contains all mustParas , then return emptyList contains empty
	 * parameter
	 * 
	 * @param req
	 *            req parameters
	 * @param mustParas
	 *            parameter must not null
	 * @return list contains empty parameters
	 */
	public static List<String> mustReq(Map<String, String> req,
			String... mustParas) {
		if (req == null) {
			return Collections.emptyList();
		}

		return Arrays
				.stream(mustParas)
				.filter(key -> !(req.containsKey(key) && ObjectUtil
						.notEmpty(req.get(key)))).collect(toList());
	}

	public static boolean anyReq(Map<String, String> req, String... paras) {
		if (req == null || req.isEmpty()) {
			return false;
		}

		return Arrays.stream(paras).anyMatch(
				key -> req.containsKey(key)
						&& ObjectUtil.notEmpty(req.get(key)));
	}

	public static boolean anyEq(String val, String... vals) {
		if (ObjectUtil.isEmpty(val)) {
			return false;
		}

		return Arrays.stream(vals).anyMatch(v -> v.equals(vals));
	}

	public final static String format(Map<String, String> paras, String charset) {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>(paras
				.keySet().size());
		for (Map.Entry<String, String> param : paras.entrySet()) {
			NameValuePair pair = new BasicNameValuePair(param.getKey(),
					param.getValue());
			paramList.add(pair);
		}
		return URLEncodedUtils.format(paramList, charset);
	}

}
