/**
 * 
 */
package jframe.core.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dzh
 * @date Sep 12, 2013 2:46:49 PM
 * @since 1.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Plugin {

	int startOrder() default 4;

	int stopOrder() default 4;

}
