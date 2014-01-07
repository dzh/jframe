/**
 * 
 */
package jframe.core.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jframe.core.conf.Config;
import jframe.core.conf.ConfigConstants;
import jframe.core.conf.DefConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Nov 20, 2013 9:46:09 AM
 * @since 1.0
 */
public class ConfigUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

	/**
	 * 根据config.properties生成一个新的文件
	 * 
	 * @param configFile
	 * @return
	 */
	public static final Config genNewConfig(String configFile) {
		Config config = new DefConfig();
		config.addConfig(ConfigConstants.FILE_CONFIG, configFile);
		// load config
		loadSystemConfig(config);
		Map<String, Set<String>> varsMap = loadConfig(config, configFile);
		String result = "";
		// replace variable
		String os = System.getProperty("os.name").toLowerCase();
		for (String var : varsMap.keySet()) {
			String val = config.getConfig(var);
			if (val == null) {
				// throw new IllegalArgumentException("Not found property key: "
				// + var);
				LOG.error("Not found property key: " + var);
				continue;
			}
			if (ConfigConstants.APP_HOME.equals(var)) {
				if (os.startsWith("windows"))
					val.replaceAll("\\\\", "/"); // handle at bat file
			}
			for (String key : varsMap.get(var)) {
				result = config.getConfig(key).replaceAll(
						"\\$\\{" + var + "\\}", val);
				config.addConfig(key, result);
			}
		}
		return config;
	}

	/**
	 * 加載系統變量到config中
	 * 
	 * @param config
	 */
	public static void loadSystemConfig(Config config) {
		Properties props = System.getProperties();
		for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements();) {
			String name = String.valueOf(e.nextElement());
			config.addConfig(name, props.getProperty(name));
		}
	}

	/**
	 * 加载配置文件中的properties
	 * 
	 * @return 变量表
	 */
	private static Map<String, Set<String>> loadConfig(Config config,
			String configFile) {
		Properties conf = new Properties();
		FileUtil.loadToProps(conf, configFile);

		// record variable
		Pattern P_VAR = Pattern.compile(ConfigConstants.REGEX_VAR,
				Pattern.CASE_INSENSITIVE);
		Map<String, Set<String>> varsMap = new HashMap<String, Set<String>>();
		for (Enumeration<?> e = conf.propertyNames(); e.hasMoreElements();) {
			String name = String.valueOf(e.nextElement());
			String value = conf.getProperty(name, "").trim();
			config.addConfig(name, value);

			Matcher m = P_VAR.matcher(value);
			while (m.find()) {
				String var = m.group(1);
				if (varsMap.containsKey(var)) {
					varsMap.get(var).add(name);
				} else {
					Set<String> setVar = new HashSet<String>();
					setVar.add(name);
					varsMap.put(var, setVar);
				}
			}
		}
		return varsMap;
	}

}
