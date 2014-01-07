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
 * @date Oct 8, 2013 10:42:40 PM
 * @since 1.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Message {

	/**
	 * <p>
	 * 相关注解:
	 * <li>DispatchAdd,</li>
	 * <li>DispatchRemove,</li>
	 * <li>MsgSend</li>
	 * </p>
	 * 
	 * @return
	 */
	boolean isSender() default false;

	/**
	 * <p>
	 * 相关注解:
	 * <li>MsgRecv</li>
	 * <li>msgInterest</li>
	 * </p>
	 * 
	 * @return
	 */
	boolean isRecver() default false;

	/**
	 * 没有types时,表示接受所有消息
	 * 
	 * @return
	 */
	int[] msgTypes() default {};

	/**
	 * 
	 * @return
	 */
	int msgMaxCache() default 1000;

	/**
	 * If true,receive ConfigMsg
	 * 
	 * @return
	 */
	boolean recvConfig() default false;

	/**
	 * If true ,receive PoisonMsg
	 * 
	 * @return
	 */
	boolean recvPoison() default false;

}
