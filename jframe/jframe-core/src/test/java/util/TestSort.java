/**
 * 
 */
package util;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import jframe.core.util.MathUtil;

/**
 * @author dzh
 * @date Mar 24, 2015 12:13:43 PM
 * @since 1.1
 */
public class TestSort {

    @Test
    @Ignore
    public void test() {
        int[] ar = new int[] { 2, 5, 1, 6, 9, 7 };
        Arrays.sort(ar);
        for (int i : ar) {
            System.out.println(i);
        }

        System.out.println(Arrays.binarySearch(ar, 5));
        System.out.println(Arrays.binarySearch(ar, 1));
    }

    @Test
    public void testMinId() {
        int[] ar = new int[] { 2, 5, 1, 6, 9, 7 };
        int id = MathUtil.calcMinNum(ar);
        System.out.println(id);

        ar = new int[] {};
        id = MathUtil.calcMinNum(ar);
        System.out.println(id);

        id = MathUtil.calcMinNum(null);
        System.out.println(id);

        ar = new int[6];
        for (int i = 0; i < ar.length;) {
            ar[i] = i;
            i++;
        }
        for (int i : ar) {
            System.out.println(i);
        }
    }

}
