/**
 * 
 */
package jframe.yunpian;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dzh
 * @date Jul 14, 2015 6:36:03 PM
 * @since 1.0
 */
public class TestYunpianService {

	@Before
	void testLoadConf() throws Exception {
		Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("jframe.yunpian/yunpian.properties");
	}

	@Test
	void testSend() {

	}

}
