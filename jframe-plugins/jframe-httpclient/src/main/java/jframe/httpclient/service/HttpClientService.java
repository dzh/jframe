/**
 * 
 */
package jframe.httpclient.service;

import java.util.Map;

import jframe.core.plugin.annotation.Service;

/**
 * <p>
 * 特性:
 * <li>TODO 同时向所有服务器发送，向指定服务器发送，向随机服务器发送</li>
 * <li>TODO 发送失败自动重发,重发fail.repeat次</li>
 * <li>TODO 支持https</li>
 * <li>TODO 发送失败异常处理</li>
 * </p>
 * 
 * @author dzh
 * @date Dec 2, 2014 12:10:16 PM
 * @since 1.0
 */
@Service(clazz = "jframe.httpclient.service.impl.HttpClientServiceImpl", id = "jframe.service.httpclient")
public interface HttpClientService {

	<T> T send(String id, String path, String data,
			Map<String, String> headers, Map<String, String> paras)
			throws Exception;

	<T> T sendGroup(String gid, String path, String data,
			Map<String, String> headers, Map<String, String> paras)
			throws Exception;

	<T> T sendRandom(String path, String data, Map<String, String> headers,
			Map<String, String> paras) throws Exception;

	<T> T sendAll(String path, String data, Map<String, String> headers,
			Map<String, String> paras) throws Exception;
}
