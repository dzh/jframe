/**
 *
 */
package jframe.aliyun.service.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import jframe.aliyun.AliyunField;
import jframe.aliyun.AliyunPlugin;
import jframe.aliyun.service.OSSService;
import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * https://help.aliyun.com/document_detail/32010.html?spm=a2c4g.11186623.6.745.735dc06dS65fFb
 *
 * // Endpoint以杭州为例，其它Region请按实际情况填写。
 * String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
 * // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
 * String accessKeyId = "<yourAccessKeyId>";
 * String accessKeySecret = "<yourAccessKeySecret>";
 * String securityToken = "<yourSecurityToken>";
 *
 * // 创建OSSClient实例。
 * OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret, securityToken);
 *
 * // 关闭OSSClient。
 * ossClient.shutdown();
 *
 * @author dzh
 * @date Feb 26, 2016 11:59:58 AM
 * @since 1.0
 */
@Injector
public class OSSServiceImpl implements OSSService {

    static Logger LOG = LoggerFactory.getLogger(OSSServiceImpl.class);

    @InjectPlugin
    static AliyunPlugin plugin;

    static String FILE_ALIOSS = "file.alioss";

    static OSSConfig _config = new OSSConfig();

    private Map<String, OSSClient> clients = new HashMap<String, OSSClient>();

    @Start
    void start() {
        LOG.info("Start OSSService");

        try {
            String file = plugin.getConfig(FILE_ALIOSS, plugin.getConfig(Config.APP_CONF) + "/alioss.properties");
            if (!new File(file).exists()) {
                throw new FileNotFoundException("not found " + file);
            }
            _config.init(file);
            for (String id : _config.getGroupIds()) {
                String endpoint = _config.getConf(id, AliyunField.K_endpoint);
                String accessKeyId = _config.getConf(id, AliyunField.K_accessKeyId);
                String accessKeySecret = _config.getConf(id, AliyunField.K_accessKeySecret);

                ClientConfiguration conf = new ClientConfiguration();
                // TODO config client
                OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret, conf);
                clients.put(id, ossClient);
            }
            LOG.info("Start OSSService Successfully!");
        } catch (Exception e) {
            LOG.error("Start OSSService Failed!" + e.getMessage(), e);
        }
    }

    @Stop
    void stop() {
        LOG.info("Stop OSSService");

        if (clients != null) for (Map.Entry<String, OSSClient> c : clients.entrySet()) {
            try {
                c.getValue().shutdown();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    @Override
    public OSSClient getOSSClient(String id) {
        return clients.get(id);
    }

}
