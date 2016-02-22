/**
 * 
 */
package jframe.launcher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * support variable feature
 * 
 * @author dzh
 * @date Feb 16, 2016 10:14:22 AM
 * @since 1.2.1
 */
public class VarProperties extends Properties {

    static Logger LOG = LoggerFactory.getLogger(VarProperties.class);

    /**
     * 
     */
    private static final long serialVersionUID = -6146614559017054452L;

    public static final Pattern P_VAR = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);

    public VarProperties(Properties defaults) {
        super(defaults);
    }

    public VarProperties() {
        super();
    }

    public synchronized void load(Reader reader) throws IOException {
        super.load(reader);
        replaceAllVar();
    }

    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
        replaceAllVar();
    }

    public synchronized void load(String file) throws IOException {
        load(new File(file));
    }

    public synchronized void load(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            load(fis);
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        } finally {
            if (file != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
        }
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return super.put(key, value);
    }

    private void replaceAllVar() {
        for (Map.Entry<Object, Object> e : entrySet()) {
            replaceVar((String) e.getKey(), (String) e.getValue(), new HashSet<String>());
        }
    }

    /**
     * TODO handle circle key
     * 
     * @param key
     * @param value
     * @keys avoid circle key
     * @return
     */
    private String replaceVar(String key, String value, Set<String> keys) {
        if (value == null)
            return null;
        Matcher m = P_VAR.matcher(value);
        if (keys.contains(key))
            throw new RuntimeException("circle key->" + key);

        String newVal = value;
        String var = null;
        String val = null;
        try {
            while (m.find()) {
                if (!keys.contains(keys)) {
                    keys.add(key);
                }

                var = m.group(1);
                val = replaceVar(var, getProperty(var), keys);
                if (val == null) {
                    continue;
                }

                newVal = newVal.replaceAll("\\$\\{" + var + "\\}", val);
            }
            keys.remove(key);

            if (containsKey(key))
                put(key, newVal);
            if (LOG.isDebugEnabled()) {
                LOG.debug("key={}, value={}->{}", key, value, newVal);
            }
        } catch (Exception e) {
            LOG.error("k->{}, v->{}, e->{}", key, value, e.getMessage());
        }
        return newVal;
    }

}
