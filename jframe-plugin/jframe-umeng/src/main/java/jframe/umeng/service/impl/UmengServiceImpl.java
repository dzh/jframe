/**
 *
 */
package jframe.umeng.service.impl;

import jframe.core.plugin.annotation.*;
import jframe.httpclient.service.HttpClientService;
import jframe.umeng.UmengConfig;
import jframe.umeng.UmengPlugin;
import jframe.umeng.service.UmengService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import push.AndroidNotification;
import push.AndroidNotification.DisplayType;
import push.UmengNotification;
import push.android.AndroidBroadcast;
import push.android.AndroidUnicast;
import push.ios.IOSBroadcast;
import push.ios.IOSUnicast;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dzh
 * @date Mar 4, 2016 10:13:18 PM
 * @since 1.0
 */
@Injector
public class UmengServiceImpl implements UmengService {

    static Logger LOG = LoggerFactory.getLogger(UmengServiceImpl.class);

    @InjectPlugin
    static UmengPlugin Plugin;

    @InjectService(id = "jframe.service.httpclient")
    static HttpClientService _http;

    static Map<String, String> HTTP_PARAS = new HashMap<String, String>(1, 1);

    static String FILE_CONF = "file.umeng";

    UmengConfig _config = new UmengConfig();

    static {
        HTTP_PARAS.put(HttpClientService.P_MIMETYPE, "application/json");
        HTTP_PARAS.put(HttpClientService.P_METHOD, "post");
    }

    @Start
    void start() {
        start(Plugin.getConfig(FILE_CONF, ""));
    }

    public void start(String path) {
        File conf = new File(path);
        if (!conf.exists()) {
            LOG.error("Not found umeng.properties {}", path);
            return;
        }

        try {
            _config.init(new FileInputStream(conf));
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return;
        }
        LOG.info("Load UmengServiceImpl successfully!");
    }

    @Stop
    void stop() {
    }

    /**
     * "alert":""/{ // 当content-available=1时(静默推送)，可选; 否则必填。
     * // 可为JSON类型和字符串类型
     * "title":"title",
     * "subtitle":"subtitle",
     * "body":"body"
     * }
     */
    @Override
    public void sendIOSUnicast(String groupId, String token, String alert, Integer badge, String sound, Map<String, String> custom)
            throws Exception {
        IOSUnicast unicast =
                new IOSUnicast(_config.getConf(groupId, UmengConfig.AppKey), _config.getConf(groupId, UmengConfig.AppMasterSecret));
        unicast.setDeviceToken(token);
        unicast.setAlert(alert);
        badge = badge == null ? 0 : badge;
        unicast.setBadge(badge);
        sound = sound == null ? "default" : sound;
        unicast.setSound(sound);
        unicast.setTestMode();
        if (custom != null) for (Map.Entry<String, String> e : custom.entrySet()) {
            unicast.setCustomizedField(e.getKey(), e.getValue());
        }

        sendUmengNotification(unicast, groupId);
    }

    /**
     * "ticker":"xx", // 必填，通知栏提示文字
     * "title":"xx", // 必填，通知标题
     * "text":"xx", // 必填，通知文字描述
     */
    @Override
    public void sendAndUnicast(String groupId, String token, String ticker, String title, String text, Map<String, String> custom)
            throws Exception {
        AndroidUnicast unicast =
                new AndroidUnicast(_config.getConf(groupId, UmengConfig.AppKey), _config.getConf(groupId, UmengConfig.AppMasterSecret));
        unicast.setDeviceToken(token);
        unicast.setTicker(ticker);
        unicast.setTitle(title);
        unicast.setText(text);
        unicast.goAppAfterOpen();
        String displayType = _config.getConf(groupId, UmengConfig.DisplayType, DisplayType.NOTIFICATION.getValue());
        unicast.setPredefinedKeyValue("display_type", displayType);
        if (DisplayType.MESSAGE.getValue().equals(displayType)) {
            unicast.setCustomField(text);
        }
        // unicast.setDisplayType(DisplayType.NOTIFICATION);
        if (custom != null) for (Map.Entry<String, String> e : custom.entrySet()) {
            unicast.setExtraField(e.getKey(), e.getValue());
        }

        sendUmengNotification(unicast, groupId);
    }

    void sendUmengNotification(UmengNotification n, String groupId) throws Exception {
        if (UmengConfig.Mode_P.equals(_config.getConf(groupId, UmengConfig.Mode))) {
            n.setProductionMode();
        }
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        n.setPredefinedKeyValue("timestamp", timestamp);
        String postBody = n.getPostBody();
        String sign = DigestUtils.md5Hex(("POST" + UmengConfig.UrlSend + postBody + n.getAppMasterSecret()).getBytes("utf-8"));
        String httpid = _config.getConf(null, UmengConfig.HttpId, "umeng");
        String path = "/api/send?sign=" + sign;

        Map<String, String> headers = new HashMap<>(1, 1);
        headers.put("User-Agent", "Mozilla/5.0");
        Object rsp = _http.send(httpid, path, postBody, headers, null);
        LOG.info("m->sendUmengNotification data->{} rsp->{}", postBody, rsp);

//        if (LOG.isDebugEnabled()) {
//            LOG.debug("m->sendUmengNotification data->{} rsp->{}", postBody, rsp);
//        }
    }

    @Override
    public void sendIOSBroadcast(String groupId, String token, String alert, Integer badge, String sound, Map<String, String> custom)
            throws Exception {
        IOSBroadcast broadcast =
                new IOSBroadcast(_config.getConf(groupId, UmengConfig.AppKey), _config.getConf(groupId, UmengConfig.AppMasterSecret));
        broadcast.setAlert(alert);
        badge = badge == null ? 0 : badge;
        broadcast.setBadge(badge);
        sound = sound == null ? "default" : sound;
        broadcast.setSound(sound);
        broadcast.setTestMode();
        if (custom != null) for (Map.Entry<String, String> e : custom.entrySet()) {
            broadcast.setCustomizedField(e.getKey(), e.getValue());
        }

        sendUmengNotification(broadcast, groupId);
    }

    @Override
    public void sendAndBroadcast(String groupId, String token, String ticker, String title, String text, Map<String, String> custom)
            throws Exception {
        AndroidBroadcast broadcast =
                new AndroidBroadcast(_config.getConf(groupId, UmengConfig.AppKey), _config.getConf(groupId, UmengConfig.AppMasterSecret));
        broadcast.setTicker(ticker);
        broadcast.setTitle(title);
        broadcast.setText(text);
        broadcast.goAppAfterOpen();
        broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        if (custom != null) for (Map.Entry<String, String> e : custom.entrySet()) {
            broadcast.setExtraField(e.getKey(), e.getValue());
        }

        sendUmengNotification(broadcast, groupId);
    }
}
