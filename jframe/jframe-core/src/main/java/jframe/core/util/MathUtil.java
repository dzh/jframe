/**
 * 
 */
package jframe.core.util;

import java.util.Collection;

/**
 * @author dzh
 * @date Sep 27, 2013 9:49:36 AM
 * @since 1.0
 */
public class MathUtil {

    /**
     * 
     * @param nums
     * @return a unused minimal natural number
     */
    public static final int calcMinNum(Collection<Integer> nums) {
        int sum = 0;
        for (int n : nums) {
            sum += n;
        }

        int n = nums.size();
        int sum_min = ((n + 1) * n) / 2;
        if (sum == sum_min)
            return n + 1;

        int x_min = n - (sum - sum_min) + 1;
        if (x_min < 1)
            x_min = 1;
        int x_max = n + (sum - sum_min);

        int x = x_min;
        while (x <= x_max) {
            if (!nums.contains(x))
                break;
            x++;
        }

        return x;
    }

    /**
     * TODO 未使用，优化
     * 
     * @param str
     * @return string hash code
     */
    public static final int calcHash(String str) {
        int h = str.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    public static final int calcCheckSum(byte[] bytes, int num) {
        return calcCheckSum(bytes, 0, bytes.length, num);
    }

    /**
     * 每次取num个byte位取反相加； 次数间异或
     * 
     * @param bytes
     * @param num
     *            每次从bytes中取num个byte
     * @return
     */
    public static final int calcCheckSum(byte[] bytes, int offset, int length, int num) {
        int cs = Integer.MAX_VALUE;
        for (int i = offset; i < length; i += num) {
            int sum = 0;
            int loc = 0;
            for (int n = 0; n < num; n++) { //
                loc = i + n;
                if (loc > offset + length || loc >= bytes.length)
                    break;
                sum += (bytes[i + n] & 0xff);
            }
            cs ^= sum;
        }
        return cs;
    }

    public static final int calcCheckSum(byte[] bytes) {
        return calcCheckSum(bytes, 1);
    }
}
