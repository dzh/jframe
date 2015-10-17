/**
 * 
 */
package jframe.pay.domain.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dzh
 * @date Jul 13, 2015 6:04:53 PM
 * @since 1.0
 */
public class ObjectUtil {
    private static final String MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    /**
     * 
     * @param obj
     * @return true if obj is not empty,else is false
     */
    public static boolean notEmpty(Object obj) {
        return (obj == null || "".equals(obj)) ? false : true;
    }

    public static boolean isEmpty(Object obj) {
        return obj == null || "".equals(obj) || "null".equals(obj);
    }

    public static boolean checkPattern(String pattern, String mobiles) {

        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    /**
     * @return the mobile
     */
    public static String getMobile() {
        return MOBILE;
    }

}
