/**
 * 
 */
package jframe.httpclient;

import jframe.ext.util.PropertiesConfig;

/**
 * @author dzh
 * @date Dec 3, 2014 11:07:29 AM
 * @since 1.0
 */
public class HttpClientConfig {

	private static final PropertiesConfig config = new PropertiesConfig();

	public static void init(String file) throws Exception {
		config.init(file);
	}

	public String getRandomGroup() {
		return "0";
	}
	
	
	
}
