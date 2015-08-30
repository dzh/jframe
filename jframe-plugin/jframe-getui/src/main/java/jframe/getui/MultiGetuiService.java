/**
 * 
 */
package jframe.getui;

import java.util.List;

import jframe.core.plugin.annotation.Service;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.base.impl.SingleMessage;

/**
 * @author dzh
 * @date Aug 20, 2015 3:52:51 PM
 * @since 1.0
 */
@Service(clazz = "jframe.getui.andriod.MultiGetuiServiceImpl", id = "jframe.service.multigetui")
public interface MultiGetuiService {

	// IPushResult push2App(ITemplate data, boolean isOffline,
	// long offlineExpireTime) throws Exception;

	IPushResult push2Single(String id, ITemplate data, boolean isOffline,
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

	IPushResult push2List(String id, ITemplate data, boolean isOffline,
			long offlineExpireTime, List<String> token);

	String getConf(String id, String key);

}
