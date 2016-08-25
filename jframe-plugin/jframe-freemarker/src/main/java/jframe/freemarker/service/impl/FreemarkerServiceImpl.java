/**
 * 
 */
package jframe.freemarker.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import jframe.core.conf.Config;
import jframe.core.conf.VarHandler;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.freemarker.FreemarkerPlugin;
import jframe.freemarker.FtlPropsConf;
import jframe.freemarker.service.FreemarkerService;

/**
 * @author dzh
 * @date Aug 25, 2016 1:53:02 PM
 * @since 1.0
 */
@Injector
public class FreemarkerServiceImpl implements FreemarkerService {
    static final Logger LOG = LoggerFactory.getLogger(FreemarkerServiceImpl.class);

    @InjectPlugin
    static FreemarkerPlugin Plugin;

    private ConcurrentMap<String, Configuration> _ftlMap = new ConcurrentHashMap<>();

    private FtlPropsConf _conf;

    @Start
    void start() {
        try {
            String file = Plugin.getConfig("file.freemarker",
                    Plugin.getConfig(Config.APP_CONF) + "/freemarker.properties");
            LOG.info("FreemarkerService starting! file-{}", file);
            _conf = new FtlPropsConf();
            _conf.init(file);
            VarHandler vh = new VarHandler(Plugin.getContext().getConfig());
            _conf.replace(vh);
            if (_ftlMap == null) {
                _ftlMap = new ConcurrentHashMap<>();
            } else {
                _ftlMap.clear();
            }
            LOG.info("FreemarkerService starting successfully!");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    @Stop
    void stop() {
        LOG.info("FreemarkerService stopped");
        _ftlMap.clear();
        _ftlMap = null;
    }

    @Override
    public Template getTemplate(String id, String ftl) throws Exception {
        Configuration conf = _ftlMap.get(id);
        if (conf == null) {
            _ftlMap.putIfAbsent(id, createConfiguration(id));
            conf = _ftlMap.get(id);
        }

        return conf.getTemplate(ftl);
    }

    private Configuration createConfiguration(String id) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        File dir = new File(_conf.getConf(id, FtlPropsConf.P_ftl_dir));
        dir.mkdirs();
        cfg.setDirectoryForTemplateLoading(dir);
        cfg.setDefaultEncoding(_conf.getConf(id, FtlPropsConf.P_ftl_encoding, "UTF-8"));
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        return cfg;
    }

}
