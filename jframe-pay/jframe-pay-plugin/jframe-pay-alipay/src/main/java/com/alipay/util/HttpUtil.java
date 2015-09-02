/**
 * 
 */
package com.alipay.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @date Nov 10, 2014 3:23:01 PM
 * @since 1.0
 */
public class HttpUtil {
	static final Logger LOG = LoggerFactory
			.getLogger(HttpUtil.class);
	
	// 签名
	public final static String SIGNATURE = "signature";

	// 签名方法
	public final static String SIGN_METHOD = "signMethod";

	public final static String CHARSET = "utf-8";
	
	public static String createLinkString(Map<String, String> para,
			boolean sort, boolean encode) {
		List<String> keys = new ArrayList<String>(para.keySet());

		if (sort)
			Collections.sort(keys);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = para.get(key);

			if (encode) {
				try {
					value = URLEncoder.encode(value, "utf-8");// TODO
				} catch (UnsupportedEncodingException e) {
				}
			}

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				sb.append(key).append(QSTRING_EQUAL).append(value);
			} else {
				sb.append(key).append(QSTRING_EQUAL).append(value)
						.append(QSTRING_SPLIT);
			}
		}
		return sb.toString();
	}

	/** = */
	public static final String QSTRING_EQUAL = "=";

	/** & */
	public static final String QSTRING_SPLIT = "&";

	private static CloseableHttpClient httpClient = HttpClients.createDefault();
	
	public static String doPost(String url, Map<String, String> headers,String params, String charset) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);
			
			httpPost.setEntity(new StringEntity(params, Charset.forName(charset)));
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, charset);
			}
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			LOG.equals(e.getMessage());
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				LOG.equals(e.getMessage());
			}
		}
		return null;
	}
}
