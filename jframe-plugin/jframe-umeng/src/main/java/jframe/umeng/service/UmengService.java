/**
 *
 */
package jframe.umeng.service;

import jframe.core.plugin.annotation.Service;

import java.util.Map;

/**
 * https://developer.umeng.com/docs/66632/detail/68343
 *
 * @author dzh
 * @date Mar 4, 2016 10:12:32 PM
 * @since 1.0
 */
@Service(clazz = "jframe.umeng.service.impl.UmengServiceImpl", id = UmengService.ID)
public interface UmengService {

    String ID = "jframe.service.umeng";

    void sendAndBroadcast(String groupId, String token, String ticker, String title, String text, Map<String, String> custom)
            throws Exception;

    void sendIOSBroadcast(String groupId, String token, String alert, Integer badge, String sound, Map<String, String> custom)
            throws Exception;

    void sendIOSUnicast(String groupId, String token, String alert, Integer badge, String sound, Map<String, String> custom)
            throws Exception;

    void sendAndUnicast(String groupId, String token, String ticker, String title, String text, Map<String, String> custom)
            throws Exception;

}
