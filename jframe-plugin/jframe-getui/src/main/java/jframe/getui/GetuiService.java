/**
 *
 */
package jframe.getui;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import jframe.core.plugin.annotation.Service;

import java.util.List;

/**
 * @author dzh
 * @date Sep 29, 2014 12:00:46 PM
 * @since 1.0
 */
@Service(clazz = "jframe.getui.andriod.GetuiServiceImpl", id = GetuiService.ID)
public interface GetuiService {
    String ID = "jframe.service.getui";

//	IPushResult push2App(ITemplate data, boolean isOffline,
//			long offlineExpireTime) throws Exception;

    IPushResult push2Single(ITemplate data, boolean isOffline,
                            long offlineExpireTime, String token) throws Exception;

    public static class Utils {

        public SingleMessage createSingleMessage(boolean isOffline,
                                                 long offlineExpireTime, ITemplate data) {
            SingleMessage message = new SingleMessage();
            message.setOffline(isOffline);
            message.setOfflineExpireTime(offlineExpireTime);
            message.setData(data);
            return message;
        }

        // public ITemplate createTemplate() {
        // PopupTransmissionTemplate template = new PopupTransmissionTemplate();
        // template.setAppId(GetuiConfig.APPID);
        // template.setAppkey(GetuiConfig.APPKEY);
        // template.setText("");
        // template.setTitle("");
        // template.setImg("");
        // template.setConfirmButtonText("");
        // template.setCancelButtonText("");
        // template.setTransmissionContent("111");
        // template.setTransmissionType(1);
        // }

    }

    IPushResult push2List(ITemplate data, boolean isOffline,
                          long offlineExpireTime, List<String> token);
}
