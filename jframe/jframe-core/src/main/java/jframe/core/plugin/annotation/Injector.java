/**
 * 
 */
package jframe.core.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * control Inject* Annotation
 * <li>目前注入导致加载过多的类,对于需要注入的类加入这个标注说明</li>
 * </p>
 * 
 * @author dzh
 * @date Sep 24, 2014 2:43:27 PM
 * @since 1.1
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Injector {

}
