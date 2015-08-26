/**
 * 
 */
package jframe.qiniu.service;

import jframe.core.plugin.annotation.Service;

import com.qiniu.util.StringMap;

/**
 * @author dzh
 * @date Jul 28, 2015 10:09:03 AM
 * @since 1.0
 */
@Service(clazz = "jframe.qiniu.service.QiniuServiceImpl", id = "jframe.service.qiniu")
public interface QiniuService {

	String uploadToken(String bucket, String key);

	String uploadToken(String bucket, String key, long expires);

	String uploadToken(String bucket, String key, long expires, StringMap policy);
	
	String uploadToken(String bucket, String key, long expires, StringMap policy, boolean strict);

	String privateDownloadUrl(String bucket, String key, long expires);

	String publicDownloadUrl(String bucket, String key);

}
