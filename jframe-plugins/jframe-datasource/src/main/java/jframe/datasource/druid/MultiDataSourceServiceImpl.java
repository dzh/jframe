/**
 * 
 */
package jframe.datasource.druid;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jframe.core.plugin.PluginException;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.datasource.DataSourcePlugin;
import jframe.datasource.DataSourceService;
import jframe.datasource.MultiDataSourceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Dec 25, 2014 10:39:46 AM
 * @since 1.0
 */
@Injector
public class MultiDataSourceServiceImpl implements MultiDataSourceService {

	static final Logger LOG = LoggerFactory
			.getLogger(MultiDataSourceServiceImpl.class);

	public static final String MULTI_DS_ID = "multi.datasource";

	public static final String PREFIX_FILE = "file.";

	@InjectPlugin
	static DataSourcePlugin _plugin;

	Map<String, DataSourceService> dsMap = new HashMap<String, DataSourceService>();

	@Start
	public void start() {
		String[] ids = _plugin.getConfig(MULTI_DS_ID, "").split("\\s+");
		for (String id : ids) {
			DruidServiceImpl ds = new DruidServiceImpl();
			try {
				ds.initDataSource(PREFIX_FILE + id);
			} catch (PluginException e) {
				LOG.error("{} -> " + e.getMessage(), id);
				System.exit(1);
			}
			dsMap.put(id, ds);
		}
	}

	@Stop
	public void stop() {
		final Map<String, DataSourceService> dsMap = this.dsMap;
		if (dsMap != null) {
			Iterator<DataSourceService> iter = dsMap.values().iterator();
			while (iter.hasNext()) {
				try {
					iter.next().closeService();
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			}
			dsMap.clear();
		}
	}

	@Override
	public DataSourceService getDataSourceService(String id) {
		return dsMap.get(id);
	}

	@Override
	public Connection getConnection(String id) throws SQLException {
		DataSourceService ds = getDataSourceService(id);
		if (ds == null)
			throw new SQLException("Not found datasource service {}", id);
		return ds.getConnection();
	}

	@Override
	public void recycleConnection(String id, Connection conn)
			throws SQLException {
		DataSourceService ds = getDataSourceService(id);
		if (ds == null)
			throw new SQLException("Not found datasource service {}", id);
		ds.recycleConnection(conn);
	}

	@Override
	public void closeConnection(String id, Connection conn) throws SQLException {
		DataSourceService ds = getDataSourceService(id);
		if (ds == null)
			throw new SQLException("Not found datasource service {}", id);
		ds.closeConnection(conn);
	}

}
