/**
 * 
 */
package jframe.core.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Service Definition
 * 
 * @author dzh
 * @date Sep 15, 2014 2:54:16 PM
 * @since 1.1
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

	/**
	 * service's identifier,it must be exclusive in a plug-in
	 * 
	 * @return
	 */
	String id();

	/**
	 * service's implement class
	 * 
	 * @return
	 */
	String clazz();

	boolean lazy() default true;

	boolean single() default true;

}
