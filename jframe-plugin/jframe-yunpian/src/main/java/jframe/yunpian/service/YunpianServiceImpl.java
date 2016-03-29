/**
 * 
 */
package jframe.yunpian.service;

import java.io.File;
import java.util.Collections;
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
import jframe.httpclient.util.HttpUtil;
import jframe.yunpian.YunpianConfig;
import jframe.yunpian.YunpianPlugin;

/**
 * @author dzh
 * @date Jul 14, 2015 6:02:24 PM
 * @since 1.0
 */
@Injector
public class YunpianServiceImpl implements YunpianService {

    @InjectPlugin
    static YunpianPlugin plugin;

    @InjectService(id = "jframe.service.httpclient")
    static HttpClientService _http;

    static Logger LOG = LoggerFactory.getLogger(YunpianServiceImpl.class);

    static String FILE_CONF = "file.yunpian";

    static YunpianConfig _config = new YunpianConfig();

    static Map<String, String> HTTP_PARAS = new HashMap<String, String>(1, 1);

    static {
        HTTP_PARAS.put(HttpClientService.P_MIMETYPE, "application/x-www-form-urlencoded");
        // HTTP_PARAS.put(HttpClientService.P_METHOD, "post");
    }

    @Start
    void start() {
        String conf = plugin.getConfig(FILE_CONF);
        if (!new File(conf).exists()) {
            LOG.error("Not found yunpian.properties -> {}", conf);
            return;
        }

        try {
            _config.init(conf);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return;
        }
        LOG.info("YunpianService start successfully!");
    }

    @Stop
    void stop() {

    }

    @Override
    public Map<String, String> send(String text, String extend, String uid, String callback, String... mobile) {
        if (mobile == null || mobile.length == 0)
            return Collections.emptyMap();

        String httpid = _config.getConf(null, YunpianConfig.HttpId, "yunpian");
        String path = _config.getConf(null, YunpianConfig.UrlSend);
        String charset = _config.getConf(null, YunpianConfig.Charset);

        int maxOnce = 100;
        for (int i = 0; i < mobile.length; i++) {
            Map<String, String> reqPara = new HashMap<String, String>();
            reqPara.put(YunpianConfig.Apikey, _config.getConf(null, YunpianConfig.Apikey));
            reqPara.put(YunpianConfig.Text, text);
            if (extend != null)
                reqPara.put(YunpianConfig.Extend, extend);
            if (uid != null) {
                reqPara.put(YunpianConfig.Uid, uid);
            }
            if (callback != null) {
                reqPara.put(YunpianConfig.Callback, callback);
            }

            int limit = Math.min(i + maxOnce - 1, mobile.length - 1);
            StringBuilder buf = new StringBuilder();
            while (true) {
                buf.append(mobile[i]);
                if (i == limit) {
                    break;
                }
                buf.append(',');
                ++i;
            }
            reqPara.put(YunpianConfig.Mobile, buf.toString());

            try {
                Map<String, String> rsp = _http.<HashMap<String, String>> send(httpid, path,
                        HttpUtil.format(reqPara, charset), null, HTTP_PARAS);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(rsp.toString());
                }
                return rsp;
            } catch (Exception e) {
                LOG.error(e.getMessage());
                // TODO
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> send(String text, String... mobile) {
        return send(text, null, null, null, mobile);
    }

}
