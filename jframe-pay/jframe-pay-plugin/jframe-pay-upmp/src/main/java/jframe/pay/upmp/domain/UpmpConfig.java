/**
 * 
 */
package jframe.pay.upmp.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.ext.util.PropertiesConfig;

/**
 * <p>
 * </p>
 * 
 * @author dzh
 * @date Nov 25, 2015 3:05:10 PM
 * @since 1.0
 */
public class UpmpConfig {

    public static final String KEY_VERSION = "version";
    public static final String KEY_CHARSET = "charset";
    public static final String KEY_MER_ID = "mer.id";
    public static final String KEY_BACK_URL = "back.url";
    public static final String KEY_FRONT_URL = "front.url";

    public static final String KEY_ACP_SDK = "acp.sdk";

    public static final String CONF_FILE_NAME = "file.upmp";

    /**
     * <pre>
     * <a href="https://open.unionpay.com/ajweb/help/faq/list?id=234&level=0&from=0">成功应答码 </a>
     * </pre>
     */
    public static final String RESPONSE_CODE_SUCCESS = "00";

    static final Logger LOG = LoggerFactory.getLogger(UpmpConfig.class);

    public static PropertiesConfig config = new PropertiesConfig();

    public static void init(String file) throws Exception {
        config.init(file);
    }

    public static String GroupID = "pay";

    public synchronized static String getConf(String key) {
        return config.getConf(GroupID, key);
    }

}
