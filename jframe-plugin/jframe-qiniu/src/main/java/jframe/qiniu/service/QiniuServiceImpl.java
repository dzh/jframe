/**
 * 
 */
package jframe.qiniu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.core.plugin.service.ServiceException;
import jframe.qiniu.QiniuConfig;
import jframe.qiniu.QiniuPlugin;

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
            if ("".equals(CONFIG.getConf(null,
                    QiniuConfig.AK))) { throw new ServiceException("Error in configuration , lost parameter -> " + QiniuConfig.AK); }
            if ("".equals(CONFIG.getConf(null,
                    QiniuConfig.SK))) { throw new ServiceException("Error in configuration , lost parameter -> " + QiniuConfig.SK); }

            AUTH = Auth.create(CONFIG.getConf(null, QiniuConfig.AK), CONFIG.getConf(null, QiniuConfig.SK));

            LOG.info("QiniuService start successfully!");
        } catch (Exception e) {
            LOG.error("QiniuService start error -> {}", e.getMessage());
        }
    }

    @Stop
    void stop() {
        CONFIG.clear();
    }

    /*
     * (non-Javadoc)
     * @see jframe.qiniu.service.QiniuService#uploadToken(java.lang.String,
     * java.lang.String)
     */
    @Override
    public String uploadToken(String id, String key) {
        String bucket = CONFIG.getConf(id, QiniuConfig.BUCKET);
        return uploadToken(bucket, key, -1L);
    }

    @Override
    public String uploadToken(String id, String key, long expires) {
        String bucket = CONFIG.getConf(id, QiniuConfig.BUCKET);
        return uploadToken(bucket, key, expires, null);
    }

    @Override
    public String privateDownloadUrl(String id, String key, long expires) {
        // String bucket = CONFIG.getConf(id, QiniuConfig.BUCKET);
        if (LOG.isDebugEnabled()) {
            LOG.debug("privateDownloadUrl -> {}", HTTP + CONFIG.getConf(id, QiniuConfig.DOMAIN) + HTTP_SEPERATOR + key);
        }
        if (expires < 1) expires = CONFIG.getConfLong(id, QiniuConfig.DOWNLOAD_EXPIRE, "3600");
        return AUTH.privateDownloadUrl(HTTP + CONFIG.getConf(id, QiniuConfig.DOMAIN) + HTTP_SEPERATOR + key, expires);
    }

    @Override
    public String publicDownloadUrl(String id, String key) {
        // String bucket = CONFIG.getConf(id, QiniuConfig.BUCKET);
        if (LOG.isDebugEnabled()) {
            LOG.debug("publicDownlaodUrl -> {}", HTTP + CONFIG.getConf(id, QiniuConfig.DOMAIN) + HTTP_SEPERATOR + key);
        }

        return HTTP + CONFIG.getConf(id, QiniuConfig.DOMAIN) + HTTP_SEPERATOR + key;
    }

    @Override
    public String uploadToken(String id, String key, long expires, StringMap policy) {
        String bucket = CONFIG.getConf(id, QiniuConfig.BUCKET);
        return uploadToken(bucket, key, expires, policy, true);
    }

    @Override
    public String uploadToken(String id, String key, long expires, StringMap policy, boolean strict) {
        String bucket = CONFIG.getConf(id, QiniuConfig.BUCKET);
        if (expires == -1) expires = Long.parseLong(CONFIG.getConf(id, QiniuConfig.UPLOAD_EXPIRE, "3600"));
        return AUTH.uploadToken(bucket, key, expires, policy, strict);
    }

}
