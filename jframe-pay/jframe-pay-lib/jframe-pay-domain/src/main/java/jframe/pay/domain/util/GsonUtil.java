/**
 * 
 */
package jframe.pay.domain.util;

import com.google.gson.Gson;

/**
 * @author dzh
 * @date Oct 17, 2014 4:37:12 PM
 * @since 1.0
 */
public class GsonUtil {

	static final Gson gson = new Gson();

	public static final String toJson(Object obj) {
		return gson.toJson(obj);
	}

	public static final <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

}
