/**
 * 
 */
package jframe.pay.wx.domain;

import jframe.ext.util.PropertiesConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 25, 2014 1:49:56 PM
 * @since 1.0
 */
public class WxConfig {

    public static final String KEY_VERSION = "version";
    public static final String KEY_CHARSET = "charset";
    public static final String KEY_SIGN_METHOD = "sign.method";

    public static final String KEY_PARTNER = "partner";
    public static final String KEY_PARTNER_KEY = "partner.key";
    public static final String KEY_APP_ID = "app.id";
    public static final String KEY_APP_SECRET = "app.secret";
    public static final String KEY_APP_KEY = "app.key";
    public static final String KEY_GRANT_TYPE = "grant.type";
    public static final String KEY_TOKEN_URL = "token.url";
    public static final String KEY_GATE_URL = "gate.url";
    public static final String KEY_NOTIFY_URL = "notify.url";

    public static final String KEY_USE_HTTPS = "use.https";

    // 成功应答码
    public static final String RESPONSE_CODE_SUCCESS = "00";

    public static final String CONF_FILE_NAME = "file.wx";
    public static final String CONF_FILE_PFX = "file.wx.pfx";

    static Logger LOG = LoggerFactory.getLogger(WxConfig.class);

    public static PropertiesConfig config = new PropertiesConfig();

    public static void init(String file) throws Exception {
        config.init(file);
    }

    public static String GroupID = "pay";

    public synchronized static String getConf(String key) {
        return config.getConf(GroupID, key);
    }

}
