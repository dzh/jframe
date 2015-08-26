/**
 * 
 */
package jframe.mybatis.ds;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author dzh
 * @date Jul 17, 2015 5:42:35 PM
 * @since 1.0
 */
public class DruidDataSourceFactory implements DataSourceFactory {

	static Logger LOG = LoggerFactory.getLogger(DruidDataSourceFactory.class);

	private DataSource dataSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.ibatis.datasource.DataSourceFactory#setProperties(java.util
	 * .Properties)
	 */
	@Override
	public void setProperties(Properties props) {
		DruidDataSource _dataSource = null;
		try {
			_dataSource = (DruidDataSource) com.alibaba.druid.pool.DruidDataSourceFactory
					.createDataSource(props);
			_dataSource.init();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		this.dataSource = _dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ibatis.datasource.DataSourceFactory#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

}
