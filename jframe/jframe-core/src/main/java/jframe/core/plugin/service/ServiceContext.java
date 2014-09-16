/**
 * 
 */
package jframe.core.plugin.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Stop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 优化存储
 * <p>
 * <li></li>
 * <li></li>
 * </p>
 * 
 * @author dzh
 * @date Sep 16, 2014 6:40:15 AM
 * @since 1.1
 */
public class ServiceContext {

	private static final Logger LOG = LoggerFactory
			.getLogger(ServiceContext.class);

	/**
	 * <ServiceID,Service>
	 */
	Map<String, Service> _svcDef;

	/**
	 * <ServiceID,List<Class>>,Class is injected Service
	 */
	Map<String, List<Class<?>>> _svcRef;

	public ServiceContext() {
		_svcDef = new HashMap<String, Service>();
		_svcRef = new HashMap<String, List<Class<?>>>();
	}

	public void regSvc(Service s) {
		if (s == null)
			return;

		synchronized (_svcDef) {
			_svcDef.put(s.getId(), s);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Create Service Instance {}.", s.toString());
		}
	}

	/**
	 * 
	 * @param id
	 *            service's id
	 * @return
	 */
	public Service getSvcById(String id) {
		if (id == null)
			return null;
		synchronized (_svcDef) {
			return _svcDef.get(id);
		}
	}

	/**
	 * Search service by service name
	 * 
	 * @param name
	 * @return
	 */
	public Service getSvcByName(String name) {
		if (name == null || "".equals(name))
			return null;

		synchronized (_svcDef) {
			for (Service svc : _svcDef.values()) {
				if (svc.getName().equals(name))
					return svc;
			}
		}
		return null;
	}

	public void unregSvc(String id) {
		synchronized (_svcDef) {
			_svcDef.remove(id);
		}
	}

	public void regSvcRef(String id, Class<?> clazz) {
		if (id == null || clazz == null)
			return;
		synchronized (_svcRef) {
			List<Class<?>> ref = _svcRef.get(id);
			ref = ref == null ? new LinkedList<Class<?>>() : ref;
			if (!ref.contains(clazz))
				ref.add(clazz);
		}
	}

	public void unregSvcRef(String id, Class<?> clazz) {
		if (id == null || clazz == null)
			return;

		synchronized (_svcRef) {
			List<Class<?>> ref = _svcRef.get(id);
			if (ref != null) {
				ref.remove(clazz);
			}

			if (ref.size() == 0) {
				_svcRef.put(id, null);
			}
		}
	}

	/**
	 * 这里假设只注入一次 inject service into obj
	 * 
	 * @param svc
	 * @param obj
	 * @param reg
	 *            regSvcRef
	 */
	public void attachService(Service svc, Class<?> clazz, boolean reg) {
		if (svc == null || clazz == null)
			return;
		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())
					&& f.isAnnotationPresent(InjectService.class)
					&& svc.getId().equalsIgnoreCase(
							f.getAnnotation(InjectService.class).id())) {
				try {
					f.setAccessible(true);
					f.set(null, svc.getSingle());
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
				break;
			}
		}

		if (reg) {
			regSvcRef(svc.getId(), clazz);
		}
	}

	/**
	 * 这里假设只注入一次
	 * 
	 * @param svc
	 * @param clazz
	 * @param unreg
	 *            unregSvcRef()
	 */
	public void detachService(Service svc, Class<?> clazz, boolean unreg) {
		if (svc == null || clazz == null)
			return;
		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())
					&& f.isAnnotationPresent(InjectService.class)
					&& svc.getId().equalsIgnoreCase(
							f.getAnnotation(InjectService.class).id())) {
				try {
					f.setAccessible(true);
					f.set(null, null);
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
				break;
			}
		}

		if (unreg) {
			unregSvcRef(svc.getId(), clazz);
		}
	}

	/**
	 * 
	 * @param id
	 *            serviceID
	 */
	public void detachService(String id) {
		Service svc = getSvcById(id);
		if (svc == null)
			return;
		synchronized (_svcRef) {
			Iterator<Class<?>> iter = _svcRef.get(id).iterator();
			while (iter.hasNext()) {
				Class<?> clazz = iter.next();
				detachService(svc, clazz, true);
				iter.remove();
			}
		}

		try {
			Service.invokeServiceMethod(svc, Stop.class);
		} catch (Exception e) {
			LOG.warn("Stop service {} waring!", svc.getName());
		}
	}

	/**
	 * 
	 */
	public void close() {
		if (_svcDef != null) {
			_svcDef.clear();
		}
		if (_svcRef != null) {
			_svcRef.clear();
		}
	}

}
