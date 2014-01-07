/**
 * 
 */
package jframe.core.plugin.dispatch;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import jframe.core.dispatch.DispatchSource;
import jframe.core.dispatch.Dispatcher;
import jframe.core.msg.Msg;
import jframe.core.plugin.Plugin;
import jframe.core.plugin.PluginRef;
import jframe.core.plugin.annotation.DispatchAdd;
import jframe.core.plugin.annotation.DispatchRemove;
import jframe.core.plugin.annotation.MsgSend;

/**
 * @author dzh
 * @date Oct 9, 2013 4:04:32 PM
 * @since 1.0
 */
public class DispatchSourceHandler implements InvocationHandler {

	private static final String M_Send = "send";

	private static final String M_AddDispatch = "addDispatch";

	private static final String M_RemoveDispatch = "removeDispatch";

	private PluginRef _ref;

	/**
	 * method cache
	 */
	private Map<String, WeakReference<Method>> _mc = new WeakHashMap<String, WeakReference<Method>>(
			6);

	public DispatchSourceHandler(PluginRef ref) {
		if (ref == null) {
			throw new NullPointerException("PluginRef is null");
		}
		this._ref = ref;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Plugin p = _ref.getPlugin();
		if (p == null) // "p==null" is impossible
			return null;

		if (method.equals(DispatchSource.class.getDeclaredMethod(M_Send,
				new Class[] { Msg.class }))) {
			Method cm = getCacheMethod(M_Send);
			if (cm != null)
				return cm.invoke(p, args);

			Class<?> clazz = p.getClass();
			do {
				for (Method m : p.getClass().getDeclaredMethods()) {
					if (m.getAnnotation(MsgSend.class) != null) {
						m.setAccessible(true);
						putCacheMethod(M_Send, m);
						return m.invoke(p, args);
					}
				}
			} while ((clazz = clazz.getSuperclass()) != null);
		} else if (method.equals(DispatchSource.class.getDeclaredMethod(
				M_AddDispatch, new Class[] { Dispatcher.class }))) {
			Method cm = getCacheMethod(M_AddDispatch);
			if (cm != null)
				return cm.invoke(p, args);

			Class<?> clazz = p.getClass();
			do {
				for (Method m : clazz.getDeclaredMethods()) {
					if (m.getAnnotation(DispatchAdd.class) != null) {
						m.setAccessible(true);
						putCacheMethod(M_Send, m);
						return m.invoke(p, args);
					}
				}
			} while ((clazz = clazz.getSuperclass()) != null);
		} else if (method.equals(DispatchSource.class.getDeclaredMethod(
				M_RemoveDispatch, new Class[] { Dispatcher.class }))) {
			Method cm = getCacheMethod(M_RemoveDispatch);
			if (cm != null)
				return cm.invoke(p, args);

			Class<?> clazz = p.getClass();
			do {
				for (Method m : clazz.getDeclaredMethods()) {
					if (m.getAnnotation(DispatchRemove.class) != null) {
						m.setAccessible(true);
						putCacheMethod(M_Send, m);
						return m.invoke(p, args);
					}
				}
			} while ((clazz = clazz.getSuperclass()) != null);
		}
		return method.invoke(p, args);
	}

	private Method getCacheMethod(String name) {
		WeakReference<Method> wr = _mc.get(name);
		if (wr != null) {
			return wr.get();
		}
		return null;
	}

	private void putCacheMethod(String name, Method m) {
		_mc.put(name, new WeakReference<Method>(m));
	}
}
