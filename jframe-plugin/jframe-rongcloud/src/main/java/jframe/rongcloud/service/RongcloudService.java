/**
 * 
 */
package jframe.rongcloud.service;

import java.util.List;
import java.util.Map;

import jframe.core.plugin.annotation.Service;
import jframe.rongcloud.Fields;

/**
 * <link>http://www.rongcloud.cn/docs/server.html</link>
 * 
 * @author dzh
 * @date Feb 14, 2016 9:39:42 PM
 * @since 1.0
 */
@Service(clazz = "jframe.rongcloud.service.impl.RongcloudServiceImpl", id = "jframe.service.rongcloud")
public interface RongcloudService extends Fields {

    String getToken(String id, Map<String, String> req);

    boolean refreshUsr(String id, Map<String, String> req);

    boolean createGroup(String id, Map<String, String> req);

    boolean joinGroupBatch(String id, Map<String, String> req);

    boolean quitGroup(String id, Map<String, String> req);

    boolean dismissGroup(String id, Map<String, String> req);

    boolean refreshGroupInfo(String id, Map<String, String> req);

    List<String> queryGroupUserList(String id, Map<String, String> req);

}
