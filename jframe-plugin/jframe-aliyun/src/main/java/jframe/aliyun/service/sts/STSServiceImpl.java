/**
 * 
 */
package jframe.aliyun.service.sts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

import jframe.aliyun.AliyunField;
import jframe.aliyun.AliyunPlugin;
import jframe.aliyun.service.STSService;
import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;

/**
 * https://help.aliyun.com/document_detail/28788.html?spm=a2c4g.11186623.6.695.uQYP1L
 * 
 * @author dzh
 * @date Feb 29, 2016 12:44:39 PM
 * @since 1.0
 */
@Injector
public class STSServiceImpl implements STSService, AliyunField {

    static Logger LOG = LoggerFactory.getLogger(STSServiceImpl.class);

    @InjectPlugin
    static AliyunPlugin plugin;

    static String FILE_ALISTS = "file.alists";

    static STSConfig _config = new STSConfig();

    private Map<String, DefaultAcsClient> clients = new HashMap<>(8);

    @Start
    void start() {
        LOG.info("Start STSService");

        try {
            String file = plugin.getConfig(FILE_ALISTS, plugin.getConfig(Config.APP_CONF) + "/alists.properties");
            if (!new File(file).exists()) { throw new FileNotFoundException("Not found sts " + file); }
            _config.init(file);
            for (String id : _config.getGroupIds()) {
                // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
                IClientProfile profile = DefaultProfile.getProfile(_config.getConf(id, K_regionId), _config.getConf(id, K_accessKeyId),
                        _config.getConf(id, K_accessKeySecret));
                DefaultAcsClient client = new DefaultAcsClient(profile);
                clients.put(id, client);
            }
        } catch (Exception e) {
            LOG.error("Start STSService Failure!" + e.getMessage(), e);
            return;
        }
        LOG.info("Start STSService Successfully!");
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
        LOG.info("Stop STSService");
    }

    @Override
    public Map<String, String> getTempAccessPerm(String id) {
        ProtocolType protocolType = ProtocolType.HTTPS;
        try {
            final AssumeRoleResponse response = assumeRole(id, _config.getConf(id, K_roleArn), _config.getConf(id, K_roleSessionName),
                    _config.getConf(id, K_policy), protocolType);

            Map<String, String> rsp = new HashMap<String, String>(3, 1);
            rsp.put(K_accessKeyId, response.getCredentials().getAccessKeyId());
            rsp.put(K_accessKeySecret, response.getCredentials().getAccessKeySecret());
            rsp.put(K_securityToken, response.getCredentials().getSecurityToken());
            return rsp;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return Collections.emptyMap();
    }

    AssumeRoleResponse assumeRole(String id, String roleArn, String roleSessionName, String policy, ProtocolType protocolType)
            throws ServerException, com.aliyuncs.exceptions.ClientException {
        DefaultAcsClient client = clients.get(id);

        // 创建一个 AssumeRoleRequest 并设置请求参数
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setVersion(_config.getConf(id, K_api_version));
        request.setMethod(MethodType.POST);
        request.setProtocol(protocolType);

        request.setRoleArn(roleArn);
        request.setRoleSessionName(roleSessionName);
        request.setPolicy(policy);

        request.setDurationSeconds(Long.parseLong(_config.getConf(id, K_durationSeconds, "3600"))); // 默认值为3600

        // 发起请求，并得到response
        final AssumeRoleResponse response = client.getAcsResponse(request);
        client.shutdown();

        return response;
    }

}
