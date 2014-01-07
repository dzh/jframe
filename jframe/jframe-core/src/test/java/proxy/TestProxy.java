/**
 * 
 */
package proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author dzh
 * @date Oct 9, 2013 8:12:42 PM
 * @since 1.0
 */
public class TestProxy {

	/**
	 * @param args
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Object proxy = Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { IOutput.class },
				new OutputHandler(new Output()));
		// System.out.println(proxy.hashCode());
		((IOutput) proxy).output("dzh");

		// Field f = proxy.getClass().getDeclaredField("h");
		Object h;
		for (Field f : proxy.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			System.out.println(f.getName());
			h = f.get(proxy);
			System.out.println(h.toString());
		}
		for (Method m : proxy.getClass().getDeclaredMethods()) {
			System.out.println(m.getName());
		}
	}

}
