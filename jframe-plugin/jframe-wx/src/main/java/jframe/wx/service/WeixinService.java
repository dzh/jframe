/**
 * 
 */
package jframe.wx.service;

import jframe.core.plugin.annotation.Service;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * @author dzh
 * @date Aug 23, 2016 11:57:41 PM
 * @since 1.0
 */
@Service(clazz = "jframe.wx.service.impl.WeixinServiceImpl", id = "jframe.service.weixin")
public interface WeixinService {

    WxMpService getWxMpService(String id);

}
