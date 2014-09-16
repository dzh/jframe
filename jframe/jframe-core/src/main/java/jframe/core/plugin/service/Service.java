/**
 * 
 */
package jframe.core.plugin.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import jframe.core.plugin.Plugin;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Support annotations:
 * <li>InjectPlugin</li>
 * <li>Start</li>
 * <li>Stop</li>
 * </p>
 * 
 * @author dzh
 * @date Sep 16, 2014 6:39:45 AM
 * @since 1.1
 */
public class Service {

	static final Logger LOG = LoggerFactory.getLogger(Service.class);

	String id;

	/**
	 * service's interface class
	 */
	String name;

	/**
	 * service's implement class
	 */
	String clazz;

	/**
	 * service's plug-in class
	 */
	Plugin plugin;

	/**
	 * service's singleton instance
	 */
	Object single;

	/**
	 * 
	 */
	ClassLoader classLoader;

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public Service setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	public String getId() {
		return id;
	}

	public Service setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Service setName(String name) {
		this.name = name;
		return this;
	}

	public String getClazz() {
		return clazz;
	}

	public Service setClazz(String clazz) {
		this.clazz = clazz;
		return this;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public Service setPlugin(Plugin plugin) {
		this.plugin = plugin;
		return this;
	}

	/**
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Object getSingle() throws ServiceException {
		synchronized (this) {
			if (single == null) {
				single = createSingle();
			}
		}
		return single;

	}

	private Object createSingle() throws ServiceException {
		Object single = null;
		try {
			Class<?> clazz = getClassLoader().loadClass(getClazz());
			single = clazz.newInstance();
			for (Field f : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(f.getModifiers())
						&& f.isAnnotationPresent(InjectPlugin.class)) {
					try {
						f.set(null, plugin);
					} catch (Exception e) {
						LOG.error(e.getMessage());
					}
					break;
				}
			}
			// start
			invokeServiceMethod(single, Start.class);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return single;
	}

	/**
	 * Invoke service start
	 * 
	 * @param svc
	 * @throws Exception
	 */
	public static void invokeServiceMethod(Object svc,
			Class<? extends Annotation> anno) throws Exception {
		Class<?> clazz = svc.getClass();
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.isAnnotationPresent(anno)) {
				m.setAccessible(true);
				m.invoke(svc);
				break;
			}
		}
	}

	public void setSingle(Object single) {
		this.single = single;
	}

	public static Service newInstance(jframe.core.plugin.annotation.Service anno)
			throws ServiceException {
		if (anno == null) {
			throw new ServiceException("Service Annotation is null");
		}
		Service svc = new Service();
		svc.setId(anno.id());
		svc.setClazz(anno.clazz());
		return svc;
	}

	@Override
	public String toString() {
		return "{ id -> " + id + ", name -> " + name + "}";
	}

}
