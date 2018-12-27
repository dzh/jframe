/**
 * 
 */
package jframe.qiniu.service;

import com.qiniu.util.StringMap;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Jul 28, 2015 10:09:03 AM
 * @since 1.0
 */
@Service(clazz = "jframe.qiniu.service.QiniuServiceImpl", id = "jframe.service.qiniu")
public interface QiniuService {

    String uploadToken(String id, String key);

    String uploadToken(String id, String key, long expires);

    String uploadToken(String id, String key, long expires, StringMap policy);

    String uploadToken(String id, String key, long expires, StringMap policy, boolean strict);

    String privateDownloadUrl(String id, String key, long expires);

    String publicDownloadUrl(String id, String key);

    String info(String id, String key);

}
