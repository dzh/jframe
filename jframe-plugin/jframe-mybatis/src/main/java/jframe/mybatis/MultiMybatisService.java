/**
 * 
 */
package jframe.mybatis;

import jframe.core.plugin.annotation.Service;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author dzh
 * @date Jul 17, 2015 11:36:43 AM
 * @since 1.0
 */
@Service(clazz = "jframe.mybatis.MultiMybatisServiceImpl", id = "jframe.service.multimybatis")
public interface MultiMybatisService {

	/**
	 * 
	 * @param id
	 *            environment[@id] in mybatis-config.xml
	 * @return
	 */
	SqlSessionFactory getSqlSessionFactory(String id);

}
