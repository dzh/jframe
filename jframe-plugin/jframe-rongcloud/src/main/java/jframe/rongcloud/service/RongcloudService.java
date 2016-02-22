/**
 * 
 */
package jframe.rongcloud.service;

import java.util.Map;

/**
 * <link>http://www.rongcloud.cn/docs/server.html</link>
 * 
 * @author dzh
 * @date Feb 14, 2016 9:39:42 PM
 * @since 1.0
 */
public interface RongcloudService {

    String getToken(Map<String, String> req);

}
