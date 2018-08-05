package jframe.qcloud.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qcloud.QcloudApiModuleCenter;
import com.qcloud.Module.Sts;
import com.qcloud.Utilities.Json.JSONObject;

import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.core.util.PropsConf;
import jframe.qcloud.QCloudConst;
import jframe.qcloud.QCloudPlugin;

/**
 * @author dzh
 * @date Aug 5, 2018 1:46:08 AM
 * @version 0.0.1
 */
@Injector
class QCloudServiceImpl implements QCloudService {

    static Logger LOG = LoggerFactory.getLogger(QCloudServiceImpl.class);

    @InjectPlugin
    static QCloudPlugin Plugin;

    static String FILE_QCLOUD = "file.qcloud";

    private PropsConf conf;

    @Start
    void start() {
        LOG.info("Start QCloudService");

        try {
            String file = Plugin.getConfig(FILE_QCLOUD, Plugin.getConfig(Config.APP_CONF) + "/qcloud.properties");
            if (!new File(file).exists()) { throw new FileNotFoundException("Not found file.qcloud " + file); }
            conf = new PropsConf();
            conf.init(file);
        } catch (Exception e) {
            LOG.error("Start QCloudService Failure!" + e.getMessage(), e);
            return;
        }
        LOG.info("Start QCloudService Successfully!");
    }

    @Stop
    void stop() {
        if (conf != null) conf.clear();

        LOG.info("Stop QCloudService");
    }

    /*
     * (non-Javadoc)
     * @see jframe.qcloud.service.QCloudService#getFederationToken()
     */
    @Override
    public Map<String, Object> getFederationToken(String id) {
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        config.put("SecretId", conf.getConf(id, QCloudConst.SECRET_ID));
        config.put("SecretKey", conf.getConf(id, QCloudConst.SECRET_KEY));

        /* 请求方法类型 POST、GET */
        config.put("RequestMethod", "GET");

        /* 区域参数，可选: gz: 广州; sh: 上海; hk: 香港; ca: 北美; 等。 */
        config.put("DefaultRegion", conf.getConf(id, QCloudConst.Region));

        QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Sts(), config);

        TreeMap<String, Object> params = new TreeMap<String, Object>();
        /* 将需要输入的参数都放入 params 里面，必选参数是必填的。 */
        /* DescribeInstances 接口的部分可选参数如下 */
        params.put("name", conf.getConf(id, QCloudConst.NAME));
        params.put("policy", conf.getConf(id, QCloudConst.POLICY));

        int durationSeconds = conf.getConfInt(id, QCloudConst.DURATION_SECONDS, "1800");
        params.put("durationSeconds", durationSeconds);

        /* 在这里指定所要用的签名算法，不指定默认为 HmacSHA1 */
        // params.put("SignatureMethod", "HmacSHA256");

        /* generateUrl 方法生成请求串, 可用于调试使用 */
        LOG.info("GetFederationToken - {}", module.generateUrl("GetFederationToken", params));

        Map<String, Object> token = new HashMap<>(4, 1);
        try {
            /* call 方法正式向指定的接口名发送请求，并把请求参数 params 传入，返回即是接口的请求结果。 */
            String result = module.call("GetFederationToken", params);
            JSONObject json_result = new JSONObject(result);
            if (json_result.getInt("code") == 0) {
                JSONObject data = json_result.getJSONObject("data");
                JSONObject credentials = data.getJSONObject("credentials");

                token.put("expiredTime", data.getLong("expiredTime"));
                token.put("sessionToken", credentials.getString("sessionToken"));
                token.put("tmpSecretId", credentials.getString("tmpSecretId"));
                token.put("tmpSecretKey", credentials.getString("tmpSecretKey"));
            } else {
                LOG.error("{} {} {}", json_result.getInt("code"), json_result.getString("codeDesc"), json_result.getString("message"));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return token;
    }

}
