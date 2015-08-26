/**
 * 
 */
package jframe.mybatis;

import jframe.core.plugin.annotation.Service;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author dzh
 * @date Sep 18, 2014 2:15:21 PM
 * @since 1.0
 */
@Service(clazz = "jframe.mybatis.MybatisServiceImpl", id = "jframe.service.mybatis")
public interface MybatisService {

	SqlSessionFactory getSqlSessionFactory();

}
