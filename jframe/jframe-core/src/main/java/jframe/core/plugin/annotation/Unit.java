/**
 * 
 */
package jframe.core.plugin.annotation;

/**
 * @author dzh
 * @date Sep 29, 2013 4:23:09 PM
 * @since 1.0
 */
public @interface Unit {

	boolean load() default true;

	int startOrder() default 4;

	int stopOrder() default 4;
}
