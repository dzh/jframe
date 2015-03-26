/**
 * 
 */
package test.client;

import org.junit.Test;

/**
 * @author dzh
 * @date Feb 12, 2015 9:36:36 AM
 * @since 1.0
 */
public class TestHttpClientService {

	public void testException() {
		try {
			throwExcep();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void throwExcep() throws Exception {
		try {
			throw new Exception("xxx");
		} finally {
			System.out.println("throw E");
		}
	}
}
