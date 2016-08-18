/**
 * 
 */
package jframe.yunsms.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.httpclient.service.HttpClientService;
import jframe.yunsms.YunsmsConfig;
import jframe.yunsms.YunsmsPlugin;
import jframe.yunsms.service.YunsmsService;

/**
 * @author dzh
 * @date Jul 9, 2016 3:36:15 PM
 * @since 1.0
 */
@Injector
public class YunsmsServiceImpl implements YunsmsService {

    static Logger LOG = LoggerFactory.getLogger(YunsmsServiceImpl.class);

    @InjectPlugin
    static YunsmsPlugin plugin;

    @InjectService(id = "jframe.service.httpclient")
    static HttpClientService _http;

    static String FILE_CONF = "file.yunsms";

    static YunsmsConfig _config = new YunsmsConfig();

    static Map<String, String> HTTP_PARAS = new HashMap<String, String>(1, 1);

    static {
        // HTTP_PARAS.put(HttpClientService.P_MIMETYPE,
        // "application/x-www-form-urlencoded");
        // HTTP_PARAS.put(HttpClientService.P_METHOD, "post");
    }

    @Override
    public boolean send(String id, String mobile, String content) {
        String data;
        try {
            data = "uid=" + _config.getConf(id, YunsmsConfig.Uid) + "&pwd=" + _config.getConf(id, YunsmsConfig.Pwd)
                    + "&mobile=" + mobile + "&content=" + java.net.URLEncoder.encode(content, "utf-8") + "&encode=utf8";

            String res = _http.<String>send(id, _config.getConf(id, YunsmsConfig.HttpTx), data, null, HTTP_PARAS);
            LOG.debug("id-{} mobile-{} res-{}", id, mobile, res);

            return "100".equals(res) ? true : false;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        return false;
    }

    @Start
    void start() {
        try {
            String conf = plugin.getConfig(FILE_CONF);
            if (!new File(conf).exists()) {
                LOG.error("Not found yunsms.properties -> {}", conf);
                return;
            }

            start(new FileInputStream(conf));
            LOG.info("YunsmsService start successfully!");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    public void start(InputStream is) throws Exception {
        _config.init(is);
        HTTP_PARAS.put("rsp.charset", _config.getConf(null, YunsmsConfig.HttpEncode, "gbk"));
    }

    @Stop
    void stop() {
        _config = null;
    }

}
