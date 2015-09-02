/**
 * 
 */
package jframe.pay.domain.util;

import java.util.Map;

import jframe.pay.domain.Fields;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;

/**
 * @author dzh
 * @date Jul 12, 2014 8:51:03 AM
 * @since 1.0
 */
public class JsonUtil {

	public static String encode(Map<String, Object> req) {
		return JSON.toJSONString(req);
	}

	public static String encode(Map<String, Object> map, SerializeFilter filter) {
		return JSON.toJSONString(map, filter);
	}

	public static Map<String, Object> decode(String res) {
		return JSON.<Map<String, Object>> parseObject(res, Map.class);
	}

	public static String toJson(Object obj) {
		if (obj == null)
			return "";
		return JSON.toJSONString(obj, new ExcludeNilPropertyFilter());
	}

	public static String toJson(Object obj, SerializeFilter filter) {
		if (obj == null)
			return "";
		return JSON.toJSONString(obj, filter);
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		if (json == null || "".equals(json))
			return null;
		return JSON.parseObject(json, clazz);
	}

	/**
	 * 过滤除此外的参数
	 * 
	 * @param params
	 * @return
	 */
	public static PropertyFilter createPropertyFilter(final String[] params) {
		return new PropertyFilter() {

			@Override
			public boolean apply(Object object, String name, Object value) {
				if (value == null)
					return false;
				if (params != null && params.length > 0) {
					for (int i = 0; i < params.length; i++) {
						if (name.equals(params[i])) {
							return true;
						}
					}
				}
				if (name.equals(Fields.F_rspCode)
						|| name.equals(Fields.F_rspDesc)) {
					return true;
				}
				return false;
			}

		};
	}

	public static PropertyFilter createExcludePropertyFilter(
			final String[] params) {
		return new PropertyFilter() {

			@Override
			public boolean apply(Object object, String name, Object value) {
				if (value == null)
					return false;

				if (params != null && params.length > 0) {
					for (int i = 0; i < params.length; i++) {
						if (name.equals(params[i])) {
							return false;
						}
					}
				}
				if (name.equals(Fields.F_rspCode)
						|| name.equals(Fields.F_rspDesc)) {
					return true;
				}
				return true;
			}

		};
	}

	public static class ExcludeNilPropertyFilter implements PropertyFilter {

		@Override
		public boolean apply(Object object, String name, Object value) {
			if (value == null || "".equals(value))
				return false;
			return true;
		}

	}

}
