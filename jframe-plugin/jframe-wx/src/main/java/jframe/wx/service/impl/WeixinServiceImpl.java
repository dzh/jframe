/**
 * 
 */
package jframe.wx.service.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.wx.WeixinPlugin;
import jframe.wx.WxPropsConf;
import jframe.wx.service.WeixinService;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;

/**
 * @ThreadSafe
 * @author dzh
 * @date Aug 23, 2016 11:58:15 PM
 * @since 1.0
 */
@Injector
public class WeixinServiceImpl implements WeixinService {

    static Logger LOG = LoggerFactory.getLogger(WeixinServiceImpl.class);

    @InjectPlugin
    static WeixinPlugin Plugin;

    private ConcurrentMap<String, WxMpService> mpMap = new ConcurrentHashMap<>();

    private WxPropsConf _conf;

    @Override
    public WxMpService getWxMpService(String id) {
        WxMpService wx = mpMap.get(id);
        if (wx == null) {
            mpMap.putIfAbsent(id, createMpService(id));
            wx = mpMap.get(id);
        }
        return wx;
    }

    private WxMpService createMpService(String id) {
        WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
        config.setAppId(_conf.getConf(id, WxPropsConf.P_appId));
        config.setSecret(_conf.getConf(id, WxPropsConf.P_secret));
        // config.setSSLContext(context);

        WxMpService mp = new WxMpServiceImpl();
        mp.setWxMpConfigStorage(config);
        return mp;
    }

    @Start
    void start() {
        LOG.info("WeixinService is starting!");
        String file = Plugin.getConfig("file.weixin", Plugin.getConfig(Config.APP_CONF) + "/weixin.properties");
        try {
            start(new FileInputStream(file));
            LOG.info("WeixinService starting successfully!");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    public void start(InputStream in) throws Exception {
        if (_conf == null) {
            _conf = new WxPropsConf();
        } else {
            _conf.clear();
        }
        _conf.init(in);
    }

    @Stop
    void stop() {
        mpMap.clear();
        mpMap = null;
        _conf.clear();
        _conf = null;
        LOG.info("WeixinService stopped!");
    }

}
