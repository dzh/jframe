package jframe.alipay.service;

import com.alipay.api.*;
import com.alipay.api.internal.util.AlipaySignature;
import jframe.alipay.AlipayPlugin;
import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.core.util.PropsConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dzh
 * @date 2019-07-22 14:58
 */
@Injector
class AlipayServiceImpl implements AlipayService {
    static Logger LOG = LoggerFactory.getLogger(AlipayServiceImpl.class);

    @InjectPlugin
    static AlipayPlugin plugin;

    static String FILE_ALIPAY = "file.alipay";

    //group id -> AlipayClient
    private Map<String, AlipayClient> clients = new HashMap<>();

    private PropsConf alipayConf;

    @Start
    void start() {
        LOG.info("Start AlipayService");
        try {
            String file = plugin.getConfig(FILE_ALIPAY, plugin.getConfig(Config.APP_CONF) + "/alipay.properties");
            if (!new File(file).exists()) {
                throw new FileNotFoundException("not found " + file);
            }
            alipayConf = new PropsConf();
            alipayConf.init(file);
            for (String id : alipayConf.getGroupIds()) {
                AlipayClient client = createAlipayClient(alipayConf, id);
                if (client != null)
                    clients.put(id, client);
            }
            LOG.info("Start AlipayService Successfully!");
        } catch (Exception e) {
            LOG.error("Start AlipayService Failed!" + e.getMessage(), e);
        }
    }

    /**
     * https://docs.open.alipay.com/270/105899/
     * <p>
     * AlipayClient alipayClient = new DefaultAlipayClient(URL,APP_ID,APP_PRIVATE_KEY,FORMAT,CHARSET,ALIPAY_PUBLIC_KEY,SIGN_TYPE);
     * <p>
     * 配置参数	示例值解释	获取方式/示例值
     * URL	支付宝网关（固定）	https://openapi.alipay.com/gateway.do
     * APPID	APPID 即创建应用后生成	获取见上方创建应用
     * APP_PRIVATE_KEY	开发者私钥，由开发者自己生成	获取见配置密钥
     * FORMAT	参数返回格式，只支持 json	json（固定）
     * CHARSET	编码集，支持 GBK/UTF-8	开发者根据实际工程编码配置
     * ALIPAY_PUBLIC_KEY	支付宝公钥，由支付宝生成	获取详见配置密钥
     * SIGN_TYPE	商户生成签名字符串所使用的签名算法类型，目前支持 RSA2 和 RSA，推荐使用 RSA2	RSA2
     *
     * @param conf
     * @param id
     * @return
     */
    private AlipayClient createAlipayClient(PropsConf conf, String id) {
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl(conf.getConf(id, F_URL, "https://openapi.alipay.com/gateway.do"));
        config.setAppId(conf.getConf(id, F_APP_ID));
        config.setPrivateKey(conf.getConf(id, F_PRIVATE_KEY));
        config.setFormat(conf.getConf(id, F_FORMAT, AlipayConstants.FORMAT_JSON));
        config.setCharset(conf.getConf(id, F_CHARSET, AlipayConstants.CHARSET_UTF8));
        config.setAlipayPublicKey(conf.getConf(id, F_PUBLIC_KEY));//alipay public key
        config.setSignType(conf.getConf(id, F_SIGN_TYPE, AlipayConstants.SIGN_TYPE_RSA2));
        String encryptKey = conf.getConf(id, F_ENCRYPT_KEY);
        if (encryptKey != null && !"".equals(encryptKey)) {
            config.setEncryptKey(encryptKey);
            config.setEncryptType(conf.getConf(id, F_ENCRYPT_TYPE, AlipayConstants.ENCRYPT_TYPE_AES));
        }
        try {
            LOG.info("createAlipayClient {}", config.getAppId());
            return new DefaultAlipayClient(config);
        } catch (AlipayApiException e) {
            LOG.error("failed to createAlipayClient " + e.getMessage(), e);
            return null;
        }
    }

    @Stop
    void stop() {
//        if (clients != null) for (Map.Entry<String, AlipayClient> c : clients.entrySet()) {
//            try {
//            } catch (Exception e) {
//                LOG.error(e.getMessage(), e.fillInStackTrace());
//            }
//        }
        clients.clear();
        LOG.info("Stop AlipayService");
    }

    @Override
    public AlipayClient getClient(String id) {
        if (id == null) return null;
        return clients.get(id);
    }

    @Override
    public boolean checkAsyncReturn(String id, Map<String, String> param) {
        try {
            return AlipaySignature.rsaCheckV2(param, getConf(id, F_PUBLIC_KEY), getConf(id, F_CHARSET), getConf(id, F_SIGN_TYPE));
        } catch (AlipayApiException e) {
            LOG.error(e.getErrMsg(), e);
        }
        return false;
    }

    @Override
    public String getConf(String id, String key) {
        return alipayConf.getConf(id, key);
    }


}
