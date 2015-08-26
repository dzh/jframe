/**
 * 
 */
package jframe.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Dec 25, 2014 10:39:06 AM
 * @since 1.0
 */
@Service(clazz = "jframe.datasource.druid.MultiDataSourceServiceImpl", id = "jframe.service.multidatasource")
public interface MultiDataSourceService {

	DataSourceService getDataSourceService(String id);

	/**
	 * 获取连接
	 */
	Connection getConnection(String id) throws SQLException;

	/**
	 * 连接返回到连接池中
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	void recycleConnection(String id, Connection conn) throws SQLException;

	/**
	 * 关闭连接
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	void closeConnection(String id, Connection conn) throws SQLException;

}
