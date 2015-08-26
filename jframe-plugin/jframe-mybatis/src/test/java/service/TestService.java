/**
 * 
 */
package service;

import org.junit.Test;

/**
 * @author dzh
 * @date Jul 24, 2015 4:53:39 PM
 * @since 1.0
 */
public class TestService {

	@Test
	public void testConfig() {
		String mybatisId = "run run_ro1";
		String[] ids = mybatisId.split("\\s+");
		for (String id : ids) {
			System.out.println(id);
		}
	}

}
