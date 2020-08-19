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
@Service(clazz = "jframe.freemarker.service.impl.FreemarkerServiceImpl", id = FreemarkerService.ID)
public interface FreemarkerService {

    String ID = "jframe.service.freemarker";

    Template getTemplate(String id, String ftl) throws Exception;

}
