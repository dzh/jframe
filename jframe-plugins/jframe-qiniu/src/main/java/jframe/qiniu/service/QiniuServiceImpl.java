/**
 * 
 */
package jframe.qiniu.service;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.core.plugin.service.ServiceException;
import jframe.qiniu.QiniuConfig;
import jframe.qiniu.QiniuPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * @author dzh
 * @date Jul 28, 2015 10:29:10 AM
 * @since 1.0
 */
@Injector
class QiniuServiceImpl implements QiniuService {

	@InjectPlugin
	static QiniuPlugin PLUGIN;

	static Logger LOG = LoggerFactory.getLogger(QiniuServiceImpl.class);

	static String FILE_CONF = "file.qiniu";

	static QiniuConfig CONFIG = new QiniuConfig();

	static Auth AUTH;

	static String HTTP = "http://";

	static String HTTP_SEPERATOR = "/";

	@Start
	void start() {
		try {
			CONFIG.init(PLUGIN.getConfig(FILE_CONF));
			if ("".equals(CONFIG.getConf(null, QiniuConfig.AK))) {
				throw new ServiceException(
						"Error in configuration , lost parameter -> "
								+ QiniuConfig.AK);
			}
			if ("".equals(CONFIG.getConf(null, QiniuConfig.SK))) {
				throw new ServiceException(
						"Error in configuration , lost parameter -> "
								+ QiniuConfig.SK);
			}

			AUTH = Auth.create(CONFIG.getConf(null, QiniuConfig.AK),
					CONFIG.getConf(null, QiniuConfig.SK));

			LOG.info("QiniuService start successfully!");
		} catch (Exception e) {
			LOG.error("QiniuService start error -> {}", e.getMessage());
		}
	}

	@Stop
	void stop() {
		CONFIG = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.qiniu.service.QiniuService#uploadToken(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String uploadToken(String bucket, String key) {
		return uploadToken(bucket, key, -1L);
	}

	@Override
	public String uploadToken(String bucket, String key, long expires) {
		return uploadToken(bucket, key, expires, null);
	}

	@Override
	public String privateDownloadUrl(String bucket, String key, long expires) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("privateDownloadUrl -> {}",
					HTTP + CONFIG.getConf(bucket, QiniuConfig.DOMAIN)
							+ HTTP_SEPERATOR + key);
		}

		return AUTH.privateDownloadUrl(
				HTTP + CONFIG.getConf(bucket, QiniuConfig.DOMAIN)
						+ HTTP_SEPERATOR + key, expires);
	}

	@Override
	public String publicDownloadUrl(String bucket, String key) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("publicDownlaodUrl -> {}",
					HTTP + CONFIG.getConf(bucket, QiniuConfig.DOMAIN)
							+ HTTP_SEPERATOR + key);
		}

		return HTTP + CONFIG.getConf(bucket, QiniuConfig.DOMAIN)
				+ HTTP_SEPERATOR + key;
	}

	@Override
	public String uploadToken(String bucket, String key, long expires,
			StringMap policy) {
		return uploadToken(bucket, key, expires, policy, true);
	}

	@Override
	public String uploadToken(String bucket, String key, long expires,
			StringMap policy, boolean strict) {
		if (expires == -1)
			expires = Long.parseLong(CONFIG.getConf(bucket,
					QiniuConfig.UPLOAD_EXPIRE, "3600"));
		return AUTH.uploadToken(bucket, key, expires, policy, strict);
	}

}
