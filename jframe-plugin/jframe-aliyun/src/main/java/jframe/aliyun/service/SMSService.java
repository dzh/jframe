package jframe.aliyun.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

import jframe.core.plugin.annotation.Service;

/**
 * 阿里云短信
 * 
 * @author dzh
 * @date Nov 19, 2018 7:00:39 PM
 * @version 0.0.1
 */
@Service(clazz = "jframe.aliyun.service.sms.SMSServiceImpl", id = "jframe.service.aliyun.sms")
public interface SMSService {

    SendSmsResponse send(String id, SendSmsRequest request) throws ClientException;

}
