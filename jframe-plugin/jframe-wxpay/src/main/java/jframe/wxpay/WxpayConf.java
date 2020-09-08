package jframe.wxpay;

import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.util.PropsConf;

import java.io.*;
import java.nio.file.Paths;

/**
 * @author dzh
 * @date 2020/8/18 19:39
 */
@Injector
public class WxpayConf extends PropsConf {

    @InjectPlugin
    static WxpayPlugin plugin;

    public static final String CERTNAME = "apiclient_cert.p12";

    public static final String P_appId = "appId";
    public static final String P_mchId = "mchId";
    public static final String P_apiKey = "apiKey";
    public static final String P_certPath = "certPath";
    public static final String P_notifyUrl = "notifyUrl";
    public static final String P_autoReport = "autoReport";
    public static final String P_useSandbox = "useSandbox";
    public static final String P_signType = "signType";

    public synchronized void init(InputStream is) throws Exception {
        super.init(is);
    }

    /**
     * @param id groupid
     * @return cert bytes
     * @throws IOException
     */
    public byte[] loadCert(String id) throws IOException {
        String certPath = this.getConf(id, P_certPath);
        File file = new File(certPath);
        if (!file.exists()) {
            file = Paths.get(plugin.getConfig(Config.APP_CONF), CERTNAME).toFile();
        }
        if (!file.exists()) {
            throw new FileNotFoundException(CERTNAME);
        }
        try (InputStream certStream = new FileInputStream(file)) {
            byte[] certData = new byte[(int) file.length()];
            certStream.read(certData);
            return certData;
        }
    }
}
