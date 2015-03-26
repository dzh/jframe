/**
 * 
 */
package util;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author dzh
 * @date Mar 24, 2015 12:13:43 PM
 * @since 1.1
 */
public class TestSort {

	@Test
	public void test() {
		int[] ar = new int[] { 2, 5, 1, 6, 9, 7 };
		Arrays.sort(ar);
		for (int i : ar) {
			System.out.println(i);
		}

		System.out.println(Arrays.binarySearch(ar, 5));
		System.out.println(Arrays.binarySearch(ar, 1));
	}

}
