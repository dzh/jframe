/**
 * 
 */
package jframe.freemarker.service;

import freemarker.template.Template;
import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Aug 25, 2016 1:50:52 PM
 * @since 1.0
 */
@Service(clazz = "jframe.freemarker.service.impl.FreemarkerServiceImpl", id = "jframe.service.freemarker")
public interface FreemarkerService {

    Template getTemplate(String id, String ftl) throws Exception;

}
