/**
 *
 */
package jframe.rongcloud.service;

import jframe.core.plugin.annotation.Service;
import jframe.rongcloud.Fields;

import java.util.List;
import java.util.Map;

/**
 * <link>http://www.rongcloud.cn/docs/server.html</link>
 *
 * @author dzh
 * @date Feb 14, 2016 9:39:42 PM
 * @since 1.0
 */
@Service(clazz = "jframe.rongcloud.service.impl.RongcloudServiceImpl", id = RongcloudService.ID)
public interface RongcloudService extends Fields {

    String ID = "jframe.service.rongcloud";

    String getToken(String id, Map<String, String> req);

    boolean refreshUsr(String id, Map<String, String> req);

    boolean createGroup(String id, Map<String, String> req);

    boolean joinGroupBatch(String id, Map<String, String> req);

    boolean quitGroup(String id, Map<String, String> req);

    boolean dismissGroup(String id, Map<String, String> req);

    boolean refreshGroupInfo(String id, Map<String, String> req);

    List<String> queryGroupUserList(String id, Map<String, String> req);

}
