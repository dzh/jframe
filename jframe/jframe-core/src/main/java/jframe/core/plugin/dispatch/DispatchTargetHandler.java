/**
 * 
 */
package jframe.core.plugin.dispatch;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import jframe.core.dispatch.DispatchTarget;
import jframe.core.msg.ConfigMsg;
import jframe.core.msg.Msg;
import jframe.core.msg.PoisonMsg;
import jframe.core.plugin.Plugin;
import jframe.core.plugin.PluginRef;
import jframe.core.plugin.annotation.Message;
import jframe.core.plugin.annotation.MsgInterest;
import jframe.core.plugin.annotation.MsgRecv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO types filter
 * 
 * @author dzh
 * @date Oct 9, 2013 4:04:58 PM
 * @since 1.0
 */
public class DispatchTargetHandler implements InvocationHandler {

	private static final Logger LOG = LoggerFactory
			.getLogger(DispatchTargetHandler.class);

	private static final String M_Receive = "receive";

	private static final String M_InterestMsg = "interestMsg";

	private PluginRef _ref;

	private BlockingQueue<Msg<?>> _cache;

	// message types
	private int[] _types;

	private Map<String, WeakReference<Method>> _mc = new WeakHashMap<String, WeakReference<Method>>(
			6);

	public DispatchTargetHandler(PluginRef ref) {
		if (ref == null) {
			throw new NullPointerException("PluginRef is null");
		}
		this._ref = ref;
		update(ref.getPlugin());
	}

	public void update(Plugin plugin) {
		if (plugin == null)
			return;
		_mc.clear();
		Message ma = plugin.getClass().getAnnotation(Message.class);
		_types = ma.msgTypes();
		if (_types != null)
			Arrays.sort(_types);
		int maxCache = ma.msgMaxCache();
		if (_cache == null) {
			_cache = new LinkedBlockingDeque<Msg<?>>(maxCache);
		} else if (maxCache != (_cache.size() + _cache.remainingCapacity())) {
			// TODO miss message
			BlockingQueue<Msg<?>> cache = new LinkedBlockingDeque<Msg<?>>(
					maxCache);
			_cache.drainTo(cache, maxCache);
			_cache = cache;
		}

		sendCache();
	}

	void sendCache() {
		if (_cache == null || _cache.size() == 0 || getPlugin() == null)
			return;

		final Plugin p = getPlugin();
		final BlockingQueue<Msg<?>> cache = _cache;
		new Thread("SendCacheMsg") {
			public void run() {
				Method im = null; // interest method
				Method rm = null; // receive method
				for (Method m : getPlugin().getClass().getDeclaredMethods()) {
					if (m.getAnnotation(MsgInterest.class) != null) {
						im = m;
						m.setAccessible(true);
					} else if (m.getAnnotation(MsgRecv.class) != null) {
						rm = m;
						m.setAccessible(true);
					}
					if (im != null && rm != null) {
						break;
					}
				}

				Msg<?> msg = null;
				while ((msg = cache.poll()) != null) {
					if (interestMsg(msg)) { // message type
						if (im != null) {
							try {
								if (!((Boolean) im.invoke(p, msg))
										.booleanValue())
									continue;
							} catch (Exception e) {
								LOG.warn(e.getMessage());
							}
						}
						if (rm != null) {
							try {
								rm.invoke(p, msg);
								LOG.info("PluginRef send cache msg: {}",
										msg.toString());
							} catch (Exception e) {
								LOG.warn(e.getMessage());
							}
						}

						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}.start();
	}

	public Plugin getPlugin() {
		return _ref.getPlugin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// cache msg when plugin is updating
		if (_ref.isUpdating() && method.getName().equals(M_InterestMsg)
				&& args.length > 0 && args[0] instanceof Msg) {
			if (interestMsg((Msg<?>) args[0])) {
				LOG.info("PluginRef recv cache msg: {}", args[0].toString());
				_cache.offer((Msg<?>) args[0]);// TODO 队列满时处理
			}
			return false;
		}

		Plugin p = _ref.getPlugin();
		if (p == null) {
			// when plug-in is updating, p is null TODO
			return null;
		}

		if (method.equals(DispatchTarget.class.getDeclaredMethod(M_InterestMsg,
				new Class[] { Msg.class }))) {
			if (args.length > 0 && args[0] instanceof Msg) {
				// special message,default return false
				try {
					if ((Msg<?>) args[0] instanceof PoisonMsg) {
						return _ref.getPlugin().getClass()
								.getAnnotation(Message.class).recvPoison();
					} else if ((Msg<?>) args[0] instanceof ConfigMsg) {
						return _ref.getPlugin().getClass()
								.getAnnotation(Message.class).recvConfig();
					}
				} catch (Exception e) {
					return false;
				}
				// custom message
				if (!interestMsg((Msg<?>) args[0]))
					return false;
				Method cm = getCacheMethod(M_InterestMsg);
				if (cm != null)
					return cm.invoke(p, args);

				Class<?> clazz = p.getClass();
				do {
					for (Method m : clazz.getDeclaredMethods()) {
						if (m.getAnnotation(MsgInterest.class) != null) {
							m.setAccessible(true);
							putCacheMethod(M_InterestMsg, m);
							return m.invoke(p, args);
						}
					}
				} while ((clazz = clazz.getSuperclass()) != null);
			}
			return false;
		} else if (method.equals(DispatchTarget.class.getDeclaredMethod(
				M_Receive, new Class[] { Msg.class }))) {
			Method cm = getCacheMethod(M_Receive);
			if (cm != null)
				return cm.invoke(p, args);

			Class<?> clazz = p.getClass();
			do {
				for (Method m : clazz.getDeclaredMethods()) {
					if (m.getAnnotation(MsgRecv.class) != null) {
						m.setAccessible(true);
						putCacheMethod(M_Receive, m);
						return m.invoke(p, args);
					}
				}
			} while ((clazz = clazz.getSuperclass()) != null);
		}

		return method.invoke(p, args);
	}

	/**
	 * @param msg
	 * @return
	 */
	boolean interestMsg(Msg<?> msg) {
		if (_types == null || _types.length == 0)
			return true;
		return Arrays.binarySearch(_types, msg.getType()) < 0 ? false : true;
	}

	private Method getCacheMethod(String name) {
		WeakReference<Method> wr = _mc.get(name);
		return null == wr ? null : wr.get();
	}

	private void putCacheMethod(String name, Method m) {
		_mc.put(name, new WeakReference<Method>(m));
	}

}
