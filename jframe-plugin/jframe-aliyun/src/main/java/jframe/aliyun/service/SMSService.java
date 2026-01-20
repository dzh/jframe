package jframe.aliyun.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import jframe.core.plugin.annotation.Service;

/**
 * 阿里云短信
 *
 * @author dzh
 * @version 0.0.1
 * @date Nov 19, 2018 7:00:39 PM
 */
@Service(clazz = "jframe.aliyun.service.sms.SMSServiceImpl", id = SMSService.ID)
public interface SMSService {

    String ID = "jframe.service.aliyun.sms";

    SendSmsResponse send(String id, SendSmsRequest request) throws ClientException;

}
