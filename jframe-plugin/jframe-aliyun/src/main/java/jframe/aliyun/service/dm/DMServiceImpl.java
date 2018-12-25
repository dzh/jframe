package jframe.aliyun.service.dm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import jframe.aliyun.AliyunField;
import jframe.aliyun.AliyunPlugin;
import jframe.aliyun.service.DMService;
import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.core.util.PropsConf;

/**
 * @author dzh
 * @date Dec 10, 2018 2:10:20 PM
 * @version 0.0.1
 */
@Injector
public class DMServiceImpl implements DMService, AliyunField {

    static Logger LOG = LoggerFactory.getLogger(DMServiceImpl.class);

    @InjectPlugin
    static AliyunPlugin plugin;

    static String FILE_ALIDM = "file.alidm";

    static PropsConf _config = new PropsConf();

    private Map<String, DefaultAcsClient> clients = new HashMap<>(8);

    @Start
    void start() {
        LOG.info("Start DMService");

        try {
            String file = plugin.getConfig(FILE_ALIDM, plugin.getConfig(Config.APP_CONF) + "/alidm.properties");
            if (!new File(file).exists()) { throw new FileNotFoundException("not found " + file); }
            _config.init(file);
            for (String id : _config.getGroupIds()) {
                // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
                IClientProfile profile = DefaultProfile.getProfile(_config.getConf(id, K_regionId), _config.getConf(id, K_accessKeyId),
                        _config.getConf(id, K_accessKeySecret));

                // https://help.aliyun.com/document_detail/96856.html?spm=a2c4g.11186623.6.588.476d3a35bcMFGs
                String regionId = _config.getConf(id, K_regionId, "cn-hangzhou");
                String product = _config.getConf(id, K_product, "Dm");
                String domain = _config.getConf(id, K_domain, "dm.aliyuncs.com");
                DefaultProfile.addEndpoint(regionId, product, domain);
                DefaultAcsClient client = new DefaultAcsClient(profile);
                clients.put(id, client);
            }
        } catch (Exception e) {
            LOG.error("Start DMService Failure!" + e.getMessage(), e);
            return;
        }
        LOG.info("Start DMService Successfully!");
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
        LOG.info("Stop DMService");
    }

    @Override
    public SingleSendMailResponse singleSend(String id, SingleSendMailRequest request) throws ClientException {
        DefaultAcsClient client = clients.get(id);
        if (client == null) { throw new NullPointerException("not found dm id:" + id); }

        return client.getAcsResponse(request);
    }

}
