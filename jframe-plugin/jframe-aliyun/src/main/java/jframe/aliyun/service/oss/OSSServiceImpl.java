/**
 * 
 */
package jframe.aliyun.service.oss;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSSClient;

import jframe.aliyun.AliyunPlugin;
import jframe.aliyun.service.OSSService;
import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;

/**
 * @author dzh
 * @date Feb 26, 2016 11:59:58 AM
 * @since 1.0
 */
@Injector
public class OSSServiceImpl implements OSSService {

    static Logger LOG = LoggerFactory.getLogger(OSSServiceImpl.class);

    @InjectPlugin
    static AliyunPlugin plugin;

    static String FILE_OSS = "file.oss";

    static OSSConfig _config = new OSSConfig();

    private Map<String, OSSClient> client = new HashMap<String, OSSClient>();

    @Start
    void start() {
        LOG.info("Start OSSService");

        try {
            String file = plugin.getConfig(FILE_OSS, plugin.getConfig(Config.APP_CONF) + "/oss-config.properties");
            if (!new File(file).exists()) { throw new FileNotFoundException("Not found oss " + file); }
            _config.init(file);
            for (String id : _config.getGroupIds()) {
                // TODO
            }
        } catch (Exception e) {
            LOG.error("Start OSSService Failure!" + e.getMessage(), e);
            return;
        }
        LOG.info("Start OSSService Successfully!");
    }

    @Stop
    void stop() {
        LOG.info("Stop OSSService");

        if (client != null) for (Map.Entry<String, OSSClient> c : client.entrySet()) {
            try {
                c.getValue().shutdown();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    @Override
    public OSSClient getOSSClient(String id) {
        return client.get(id);
    }

}
