/**
 * 
 */
package hash;

import org.junit.Test;

/**
 * @author dzh
 * @date Sep 28, 2013 2:57:09 PM
 * @since 1.0
 */
public class TestHash {

	@Test
	public void testHash() {
		String str = "dzh";
		System.out.println(str.hashCode()); // 99986
		str = str + "0";
		System.out.println(str.hashCode()); // 3099614
		str = str + "1";
		System.out.println(str.hashCode()); // 96088083
	}

}
