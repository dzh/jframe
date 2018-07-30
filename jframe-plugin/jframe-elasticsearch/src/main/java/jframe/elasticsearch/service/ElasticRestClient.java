/**
 * 
 */
package jframe.elasticsearch.service;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Sep 29, 2016 7:40:12 PM
 * @since 1.0
 */
@Service(clazz = "jframe.elasticsearch.service.impl.ElasticRestClientImpl", id = "jframe.elasticsearch.rest")
public interface ElasticRestClient {

    String HTTP_HOST = "es.http.host"; // [http://]ip:port
    String HTTP_SOCKET_TIMEOUT = "es.http.socket.timeout"; // ms
    String HTTP_CONN_TIMEOUT = "es.http.conn.timeout"; // connect timeout ms

}
