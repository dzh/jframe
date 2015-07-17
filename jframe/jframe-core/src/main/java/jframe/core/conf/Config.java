/**
 * 
 */
package jframe.core.conf;

import java.util.Map;

import jframe.core.Frame;

/**
 * <p>
 * Frame's Configuration
 * <li>load config from config.properties</li>
 * <li></li>
 * </p>
 * 
 * @ThreadSafe
 * @author dzh
 * @date Sep 12, 2013 9:17:47 PM
 * @since 1.0
 */
public interface Config extends ConfigConstants {

	void setFrame(Frame frame);

	Frame getFrame();

	/**
	 * 
	 * 
	 * @param key
	 * @param value
	 */
	void addConfig(String key, String value);

	String getConfig(String key);

	String getConfig(String key, String defVal);

	/**
	 * <p>
	 * <li>if doesn't contain the key ,then return null.</li>
	 * <li>if contain the key, then return old value</li>
	 * </p>
	 * 
	 * @param key
	 * @param value
	 * @return old value or null
	 */
	String modifyConfig(String key, String value);

	void clearConfig();

	/**
	 * @since 1.2.0
	 * @return all config
	 */
	Map<String, String> getConfig();

}
