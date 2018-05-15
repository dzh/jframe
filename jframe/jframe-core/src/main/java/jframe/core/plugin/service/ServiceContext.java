/**
 * 
 */
package jframe.core.plugin.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Stop;

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

    private static final Logger LOG = LoggerFactory.getLogger(ServiceContext.class);

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
        if (s == null) return;

        synchronized (_svcDef) {
            _svcDef.put(s.getId(), s);
        }
        LOG.debug("Create Service Instance {}.", s.toString());
    }

    /**
     * 
     * @param id
     *            service's id
     * @return
     */
    public Service getSvcById(String id) {
        if (id == null) return null;
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
        if (name == null || "".equals(name)) return null;

        synchronized (_svcDef) {
            for (Service svc : _svcDef.values()) {
                if (svc.getName().equals(name)) return svc;
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
        if (id == null || clazz == null) return;
        synchronized (_svcRef) {
            List<Class<?>> ref = _svcRef.get(id);
            if (ref == null) {
                ref = new LinkedList<Class<?>>();
                _svcRef.put(id, ref);
            }
            if (!ref.contains(clazz)) ref.add(clazz);
        }
    }

    public void unregSvcRef(String id, Class<?> clazz) {
        if (id == null || clazz == null) return;

        synchronized (_svcRef) {
            List<Class<?>> ref = _svcRef.get(id);
            if (ref != null) {
                ref.remove(clazz);
            }

            if (ref.size() == 0) {
                _svcRef.remove(id);
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
    @Deprecated
    public void attachService(Service svc, Class<?> clazz, boolean reg) {
        if (svc == null || clazz == null) return;
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.isAnnotationPresent(InjectService.class)
                    && svc.getId().equals(f.getAnnotation(InjectService.class).id())) {
                attachService(svc, f, reg);
                break;
            }
        }

        if (reg) {
            regSvcRef(svc.getId(), clazz);
        }
    }

    /**
     * TODO error if plug-in has not started
     * 
     * @param svc
     * @param f
     * @param reg
     */
    public void attachService(Service svc, Field f, boolean reg) {
        if (svc == null || f == null) return;

        if (hasInjected(svc, f.getDeclaringClass())) return;

        try {
            f.setAccessible(true);
            f.set(null, svc.getSingle());
            if (reg) {
                regSvcRef(svc.getId(), f.getDeclaringClass());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }

        LOG.debug("AttachService {} -> {}", svc.toString(), f.getDeclaringClass());
    }

    boolean hasInjected(Service svc, Class<?> clazz) {
        if (svc == null || clazz == null) return false;

        synchronized (_svcRef) {
            List<Class<?>> list = _svcRef.get(svc.getId());
            if (list == null) return false;
            return list.contains(clazz);
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
        if (svc == null || clazz == null) return;
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.isAnnotationPresent(InjectService.class)
                    && svc.getId().equals(f.getAnnotation(InjectService.class).id())) {
                try {
                    f.setAccessible(true);
                    f.set(null, null);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
                break;
            }
        }

        if (unreg) {
            unregSvcRef(svc.getId(), clazz);
        }

        LOG.debug("DetachService {} -> {}", svc.toString(), clazz);
    }

    /**
     * 
     * @param id
     *            serviceID
     */
    public void detachService(String id) {
        Service svc = getSvcById(id);
        if (svc == null) return;

        try {
            synchronized (_svcRef) {
                if (_svcRef.get(id) != null) {
                    for (Object clazz : _svcRef.get(id).toArray()) {
                        detachService(svc, (Class<?>) clazz, true);
                    }
                }
            }
        } finally {
            try {
                if (svc.getSingle(false) != null) {
                    try {
                        Service.invokeServiceMethod(svc.getSingle(), Stop.class);
                    } catch (Exception e) {
                        LOG.warn("Stop service {}!", svc.toString());
                    }
                }
            } catch (ServiceException e) {}
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
