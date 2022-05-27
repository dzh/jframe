/**
 * 
 */
package jframe.launcher.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * <p>
 * <li>NotThreadSafe</li>
 * <li>support variable definition</li>
 * </p>
 * 
 * @author dzh
 * @date Jan 31, 2016 3:16:27 PM
 * @since 1.2
 */
public class DefConfig implements Config {

    private Map<String, String> _config; //

    public DefConfig() {
        _config = newMap(32);
    }

    public DefConfig(Properties props) {
        _config = newMap(props.size());

        for (Map.Entry<Object, Object> e : props.entrySet()) {
            _config.put((String) e.getKey(), (String) e.getValue());
        }
    }

    protected Map<String, String> newMap(int size) {
        return new HashMap<String, String>(size, 1);
    }

    public DefConfig(Config config) {

    }

    public String getConfig(String k) {
        String v = _config.get(k);
        if (v == null) v = System.getProperty(k);
        return v;
    }

    public String getConfig(String k, String defval) {
        String v = _config.get(k);
        return v == null ? defval : v;
    }

    public String setConfig(String k, String v) {
        return _config.put(k, v);
    }

    public boolean contain(String k) {
        return _config.containsKey(k);
    }

    public Set<String> keySet() {
        return _config.keySet();
    }

    /**
     * 利用config中的变量值，替换value里的变量
     * 
     * @param v
     * @return
     */
    public String replace(String input) {
        return replace(this, input);
    }

    /**
     * replace all variables in input
     * 
     * @param config
     *            jvm系统变量，一般是System.getProperty
     * @param input
     * @return
     */
    public static String replace(Config config, String input) {
        Matcher m = P_VAR.matcher(input);
        String var = null;
        String val = null;
        while (m.find()) {
            var = m.group(1);
            val = config.getConfig(var);
            if (val == null) {
                // LOG.warn("Not found variable's value: " + var);
                continue;
            }

            input = input.replaceAll("\\$\\{" + var + "\\}", val);
        }
        return input;
    }

    public boolean hasVar(String input) {
        return P_VAR.matcher(input).find();
    }

}
