/**
 * 
 */
package jframe.mybatis.ds;

import java.util.Properties;

import javax.sql.DataSource;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.datasource.DataSourceService;
import jframe.mybatis.MybatisPlugin;

import org.apache.ibatis.datasource.DataSourceFactory;

/**
 * @author dzh
 * @date Sep 18, 2014 1:57:51 PM
 * @since 1.0
 */
@Injector
public class JframeDataSourceFactory implements DataSourceFactory {

	@InjectPlugin
	static MybatisPlugin plugin;

	@InjectService(id = "jframe.service.datasource")
	static DataSourceService dataSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ibatis.datasource.DataSourceFactory#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {
		return dataSource.getDataSource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.ibatis.datasource.DataSourceFactory#setProperties(java.util
	 * .Properties)
	 */
	@Override
	public void setProperties(Properties arg0) {

	}

}
