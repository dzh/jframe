/**
 * 
 */
package jframe.launcher.api;

import java.util.Set;

/**
 * 
 * @author dzh
 * @date Jan 21, 2016 4:57:11 PM
 * @since 1.0
 */
public interface Config extends LauncherConstants {

    String getConfig(String k);

    String getConfig(String k, String defval);

    String setConfig(String k, String v);

    boolean contain(String k);

    Set<String> keySet();

}
