/**
 * 
 */
package jframe.core.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inject service object to a clazz's field
 * <p>
 * Search order:
 * <li></li>
 * <li></li>
 * </p>
 * 
 * @author dzh
 * @date Sep 15, 2014 3:15:15 PM
 * @since 1.1
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectService {

	/**
	 * service's id
	 * 
	 * @return
	 */
	String id();

	// String plugin() default "";

}
