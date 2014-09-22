/**
 * 
 */
package jframe.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import jframe.core.plugin.annotation.Service;

/**
 * 
 * @author dzh
 * @date Oct 17, 2013 5:12:53 PM
 * @since 1.0
 */
@Service(clazz = "jframe.datasource.druid.DruidServiceImpl", id = "jframe.service.datasource")
public interface DataSourceService {

	/**
	 * 获取连接
	 */
	Connection getConnection() throws SQLException;

	/**
	 * 连接返回到连接池中
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	void recycleConnection(Connection conn) throws SQLException;

	/**
	 * 关闭连接
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	void closeConnection(Connection conn) throws SQLException;

	DataSource getDataSource();

}
