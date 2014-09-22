/**
 * 
 */
package jframe.mybatis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 18, 2014 2:17:43 PM
 * @since 1.0
 */
class MybatisServiceImpl implements MybatisService {

	static Logger LOG = LoggerFactory.getLogger(MybatisServiceImpl.class);

	/**
	 * mybatis config
	 */
	static String FILE_MYBATIS = "file.mybatis";

	@InjectPlugin
	static MybatisPlugin plugin;

	private SqlSessionFactory sqlSessionFactory;

	@Start
	void start() {
		File mcFile = new File(plugin.getConfig(FILE_MYBATIS, ""));
		if (!mcFile.exists()) {
			LOG.error("Not found mybatis-config, {}" + mcFile.getAbsolutePath());
			return;
		}
		Reader mybatis = null;
		try {
			mybatis = new InputStreamReader(new FileInputStream(mcFile),
					"utf-8");
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatis);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			if (mybatis != null)
				try {
					mybatis.close();
				} catch (IOException e) {
				}
		}
		LOG.info("MybatisServiceImpl start successfully!");
	}

	@Stop
	void Stop() {
		sqlSessionFactory = null;
		LOG.info("MybatisServiceImpl stop successfully!");
	}

	@Override
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

}
