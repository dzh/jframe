/**
 * 
 */
package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author dzh
 * @date Oct 9, 2013 8:13:15 PM
 * @since 1.0
 */
public class OutputHandler implements InvocationHandler {

	private Object obj;

	public OutputHandler(Object obj) {
		this.obj = obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = obj.getClass().getMethods()[0].invoke(obj, args);
		return result;
	}

}
