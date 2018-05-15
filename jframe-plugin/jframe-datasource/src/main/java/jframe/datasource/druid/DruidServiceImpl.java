/**
 * 
 */
package jframe.datasource.druid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

import jframe.core.plugin.PluginException;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.datasource.DataSourcePlugin;
import jframe.datasource.DataSourceService;

/**
 * @author dzh
 * @date Oct 21, 2013 2:32:51 PM
 * @since 1.0
 */
@Injector
public class DruidServiceImpl implements DataSourceService {

    private static final Logger LOG = LoggerFactory.getLogger(DruidServiceImpl.class);

    public static final String DS_CONFIG = "file.datasource";

    private DruidDataSource _dataSource;

    @InjectPlugin
    static DataSourcePlugin _plugin;

    @Start
    public void start() {
        try {
            initDataSource(_plugin.getConfig(DS_CONFIG));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Stop
    public void stop() {
        if (_dataSource != null) _dataSource.close();
    }

    /*
     * (non-Javadoc)
     * @see datam.plugin.services.DataSourcePoolService#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException {
        return _dataSource.getConnection();
    }

    public void initDataSource(String path) throws PluginException {
        File dsFile = new File(path);
        if (!dsFile.exists()) {
            LOG.error("Not found datasource file {}", path);
            return;
        }

        Properties p = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dsFile);
            p.load(fis);
            _dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(p);
            _dataSource.init();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            System.exit(-1); // 数据库初始化失败，程序直接退出

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {}
            }
        }
    }

    @Override
    public void recycleConnection(Connection conn) throws SQLException {
        ((DruidPooledConnection) conn).recycle();
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        ((DruidPooledConnection) conn).close();
    }

    @Override
    public DataSource getDataSource() {
        return _dataSource;
    }

    @Override
    public void closeService() {
        if (_dataSource != null) {
            _dataSource.close();
        }
        LOG.info("DataSource is closed");
    }

}
