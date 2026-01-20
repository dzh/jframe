/**
 *
 */
package jframe.httpclient.service;

import jframe.core.plugin.annotation.Service;

import java.util.Map;

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
@Service(clazz = "jframe.httpclient.service.impl.HttpClientServiceImpl", id = HttpClientService.ID)
public interface HttpClientService {

    String ID = "jframe.service.httpclient";

    String P_MIMETYPE = "mimeType";
    String P_METHOD = "method";

    String send(String id, String path, String data, Map<String, String> headers, Map<String, String> paras) throws Exception;

    // <T> T sendGroup(String gid, String path, String data,
    // Map<String, String> headers, Map<String, String> paras)
    // throws Exception;

    // <T> T sendRandom(String path, String data, Map<String, String> headers,
    // Map<String, String> paras) throws Exception;

    // <T> T sendAll(String path, String data, Map<String, String> headers,
    // Map<String, String> paras) throws Exception;
}
