package jframe.aliyun.service.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import jframe.aliyun.AliyunField;
import jframe.aliyun.AliyunPlugin;
import jframe.aliyun.service.SMSService;
import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.core.util.PropsConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * https://help.aliyun.com/document_detail/55284.html?spm=a2c4g.11186623.6.566.5d1b4175GzAYkw
 * <p>
 * https://helpcdn.aliyun.com/document_detail/68360.html
 *
 * @author dzh
 * @version 0.0.1
 * @date Nov 19, 2018 7:01:24 PM
 */
@Injector
public class SMSServiceImpl implements SMSService, AliyunField {

    static Logger LOG = LoggerFactory.getLogger(SMSServiceImpl.class);

    @InjectPlugin
    static AliyunPlugin plugin;

    static String FILE_ALISMS = "file.alisms";

    static PropsConf _config = new PropsConf();

    private Map<String, DefaultAcsClient> clients = new HashMap<>(8);

    @Start
    void start() {
        LOG.info("Start SMSService");

        try {
            String file = plugin.getConfig(FILE_ALISMS, plugin.getConfig(Config.APP_CONF) + "/alisms.properties");
            if (!new File(file).exists()) {
                throw new FileNotFoundException("not found " + file);
            }
            _config.init(file);
            for (String id : _config.getGroupIds()) {
                // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
                IClientProfile profile = DefaultProfile.getProfile(_config.getConf(id, K_regionId), _config.getConf(id, K_accessKeyId),
                        _config.getConf(id, K_accessKeySecret));
                String regionId = _config.getConf(id, K_regionId, "cn-hangzhou");
                String product = _config.getConf(id, K_product, "Dysmsapi");
                String domain = _config.getConf(id, K_domain, "dysmsapi.aliyuncs.com");
                DefaultProfile.addEndpoint(regionId, product, domain);

                DefaultAcsClient client = new DefaultAcsClient(profile);
                clients.put(id, client);
            }
            LOG.info("Start SMSService Successfully!");
        } catch (Exception e) {
            LOG.error("Start SMSService Failed!" + e.getMessage(), e);
        }
    }

    @Stop
    void stop() {
        for (Entry<String, DefaultAcsClient> client : clients.entrySet()) {
            try {
                client.getValue().shutdown();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        LOG.info("Stop SMSService");
    }

    @Override
    public SendSmsResponse send(String id, SendSmsRequest request) throws ClientException {
        DefaultAcsClient client = clients.get(id);
        if (client == null) {
            throw new NullPointerException("not found sms id:" + id);
        }

        return client.getAcsResponse(request);// TODO async
    }

}
