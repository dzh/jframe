/**
 * 
 */
package jframe.mybatis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;

/**
 * <p>
 * three parameters in config.properties:
 * <li>file.mybatis</li>
 * <li>mybatis.id</li>
 * <li>file.mybatis.charset (optional,utf-8 is default)</li>
 * </p>
 * 
 * @author dzh
 * @date Jul 17, 2015 1:57:11 PM
 * @since 1.0
 */
@Injector
public class MultiMybatisServiceImpl implements MultiMybatisService {

    static Logger LOG = LoggerFactory.getLogger(MultiMybatisServiceImpl.class);

    /**
     * mybatis config
     */
    static String FILE_MYBATIS = "file.mybatis";

    /**
     * mybatis config file charset
     */
    static String MYBATIS_CHARSET = "file.mybatis.charset";

    /**
     * start using mybatis's environments, or else not be used
     */
    static String MYBATIS_ID = "mybatis.id";

    static String FILE_CHARSET = "utf-8";

    @InjectPlugin
    static MybatisPlugin plugin;

    Map<String, SqlSessionFactory> fac;

    @Start
    void start() {
        String[] envs = plugin.getConfig(MYBATIS_ID, "").split("\\s+");
        if (envs.length == 0) {
            LOG.error("mybatis.id is empty, must define a enviroment id at least.");
            return;
        }

        File mcFile = new File(plugin.getConfig(FILE_MYBATIS, ""));
        if (!mcFile.exists()) {
            LOG.error("Not found mybatis-config, {}" + mcFile.getAbsolutePath());
            return;
        }
        FILE_CHARSET = plugin.getConfig(MYBATIS_CHARSET, "utf-8");
        Resources.setDefaultClassLoader(plugin.getPluginClassLoader());

        fac = new HashMap<String, SqlSessionFactory>(envs.length, 1);
        for (String id : envs) {
            SqlSessionFactory factory = loadFactory(id);
            if (factory == null) { // TODO
                LOG.error("Not found SqlSessionFactory id -> {}", id);
            }
            fac.put(id, factory);
        }

        LOG.info("MultiMybatisServiceImpl start successfully!");
    }

    @Stop
    void Stop() {
        LOG.info("MybatisServiceImpl stop successfully!");
    }

    /**
     * 
     */
    @Override
    public SqlSessionFactory getSqlSessionFactory(String id) {
        SqlSessionFactory ssf = fac.get(id);
        if (ssf == null) LOG.error("Not found SqlSessionFactory id -> {}", id);
        return ssf;
    }

    private SqlSessionFactory loadFactory(String id) {
        File mcFile = new File(plugin.getConfig(FILE_MYBATIS));
        Reader mybatis = null;
        try {
            mybatis = new InputStreamReader(new FileInputStream(mcFile));
            return new SqlSessionFactoryBuilder().build(mybatis, id);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        } finally {
            if (mybatis != null) try {
                mybatis.close();
            } catch (IOException e) {}
        }
        return null;
    }
}
