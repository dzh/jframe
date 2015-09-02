/**
 * 
 */
package jframe.pay.http.handler;

/**
 * @author dzh
 * @date Sep 22, 2014 5:39:54 PM
 * @since 3.0
 */
public class Dispatcher {

	private String id;
	private String url;
	private String clazz;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	@Override
	public String toString() {
		return "id -> " + id + ", url -> " + url + ", clazz -> " + clazz;
	}
}
