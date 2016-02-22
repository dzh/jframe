/**
 * 
 */
package jframe.core.conf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author dzh
 * @date Oct 11, 2013 1:12:37 PM
 * @since 1.0
 */
@Deprecated
public class VarHandler {

    // private static final Logger LOG =
    // LoggerFactory.getLogger(VarHandler.class);

    public static final Pattern P_VAR = Pattern.compile(Config.REGEX_VAR, Pattern.CASE_INSENSITIVE);

    private Config _config;

    public VarHandler(Config config) {
        this._config = config;
    }

    /**
     * 利用config中的变量值，替换value里的变量
     * 
     * @param v
     * @return
     */
    public String replace(String input) {
        return replace(_config, input);
    }

    /**
     * 
     * @param system
     *            jvm系统变量，一般是System.getProperty
     * @param input
     * @return
     */
    public String replace(Config config, String input) {
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
