package jframe.aliyun.service;

import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import jframe.core.plugin.annotation.Service;

/**
 * 邮件推送服务
 *
 * @author dzh
 * @version 0.0.1
 * @date Dec 10, 2018 1:46:37 PM
 */
@Service(clazz = "jframe.aliyun.service.dm.DMServiceImpl", id = DMService.ID)
public interface DMService {

    String ID = "jframe.service.aliyun.dm";

    SingleSendMailResponse singleSend(String id, SingleSendMailRequest request) throws ClientException;

}