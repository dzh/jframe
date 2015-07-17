/**
 * 
 */
package jframe.mybatis.ds;

import java.util.Properties;

import javax.sql.DataSource;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.datasource.MultiDataSourceService;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Jul 17, 2015 5:36:38 PM
 * @since 1.0
 */
@Injector
public class JfameMultiDataSourceFactory implements DataSourceFactory {

	static final Logger LOG = LoggerFactory
			.getLogger(JfameMultiDataSourceFactory.class);

	@InjectService(id = "jframe.service.multidatasource")
	static MultiDataSourceService multiDataSource;

	private String id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.ibatis.datasource.DataSourceFactory#setProperties(java.util
	 * .Properties)
	 */
	@Override
	public void setProperties(Properties props) {
		id = props.getProperty("id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ibatis.datasource.DataSourceFactory#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {
		try {
			return multiDataSource.getDataSourceService(id).getDataSource();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

}
