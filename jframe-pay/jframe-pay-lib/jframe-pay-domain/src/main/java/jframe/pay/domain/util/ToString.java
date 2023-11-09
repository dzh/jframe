/**
 *
 */
package jframe.pay.domain.util;

import java.util.Map;

/**
 * @author dzh
 * @date Aug 15, 2014 5:26:56 PM
 * @since 1.0
 */
@Deprecated
public class ToString {

    public static final String toString(Map<?, ?> map) {
        if (map == null || map.isEmpty())
            return "";
        StringBuilder buf = new StringBuilder();
        buf.append("\nbegin\n");
        for (Object o : map.keySet()) {
            buf.append(String.valueOf(o) + "->" + String.valueOf(map.get(o))
                    + ",");
        }
        buf.append("\nend\n");
        return buf.toString();
    }

}
