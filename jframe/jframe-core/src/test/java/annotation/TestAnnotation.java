/**
 * 
 */
package annotation;

import java.lang.reflect.Field;

import jframe.core.plugin.annotation.Message;
import jframe.core.plugin.annotation.Plugin;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author dzh
 * @date Oct 14, 2013 10:09:13 AM
 * @since 1.0
 */
public class TestAnnotation {

	@Test
	public void testInherited() throws NoSuchFieldException, SecurityException {
		// sub
		SubClazz sub = new SubClazz();
		Message msg = sub.getClass().getAnnotation(Message.class);
		Assert.assertEquals(false, msg.isSender());
		Assert.assertEquals(true, msg.isRecver());
		Plugin plgn = sub.getClass().getAnnotation(Plugin.class);
		Assert.assertNotNull(plgn);

		// super
		plgn = sub.getClass().getSuperclass().getAnnotation(Plugin.class);
		Assert.assertNotNull(plgn);

		// field
		Field f = sub.getClass().getField("str");
		System.out.println(f.getDeclaringClass());
		System.out.println(f.getType());
	}

}
