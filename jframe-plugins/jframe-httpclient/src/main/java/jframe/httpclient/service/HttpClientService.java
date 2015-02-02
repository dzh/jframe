/**
 * 
 */
package jframe.httpclient.service;

import java.util.Map;

import jframe.core.plugin.annotation.Service;

/**
 * <p>
 * 特性:
 * <li>同时向所有服务器发送，向指定服务器发送，向随机服务器发送</li>
 * <li>发送失败自动重发,重发fail.repeat次</li>
 * <li>TODO 支持https</li>
 * </p>
 * 
 * @author dzh
 * @date Dec 2, 2014 12:10:16 PM
 * @since 1.0
 */
@Service(clazz = "jframe.httpclient.service.impl.HttpClientServiceImpl", id = "jframe.service.httpclient")
public interface HttpClientService {

	String M_POST = "post";
	String M_GET = "get";

	String P_HTTPS = "https";
	String P_HTTP = "http";

	void send(String id, String data, Map<String, String> http,
			Map<String, String> para);

	void sendGroup(String gid, String data, Map<String, String> http,
			Map<String, String> para);

	void sendRandom(String data, Map<String, String> http,
			Map<String, String> para);

	void sendAll(String data, Map<String, String> http, Map<String, String> para);
}
