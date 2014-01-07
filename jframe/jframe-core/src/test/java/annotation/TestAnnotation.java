/**
 * 
 */
package annotation;

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
	public void testInherited() {
		//sub
		SubClazz sub = new SubClazz();
		Message msg = sub.getClass().getAnnotation(Message.class);
		Assert.assertEquals(false, msg.isSender());
		Assert.assertEquals(true, msg.isRecver());
		Plugin plgn = sub.getClass().getAnnotation(Plugin.class);
		Assert.assertNotNull(plgn);
		
		//super
		plgn = sub.getClass().getSuperclass().getAnnotation(Plugin.class);
		Assert.assertNotNull(plgn);
	}

}
