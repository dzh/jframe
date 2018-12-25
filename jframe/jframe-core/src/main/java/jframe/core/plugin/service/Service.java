/**
 * 
 */
package jframe.core.plugin.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.Plugin;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.loader.PluginClassLoader;

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
     * service's singleton instance
     */
    Object single;

    /**
     * 
     */
    PluginClassLoader classLoader;

    public PluginClassLoader getClassLoader() {
        return classLoader;
    }

    public Service setClassLoader(PluginClassLoader classLoader) {
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
        return classLoader.getPlugin();
    }

    /**
     * 
     * @return
     * @throws ServiceException
     */
    public synchronized Object getSingle() throws ServiceException {
        return getSingle(true);

    }

    /**
     * 
     * @return
     * @throws ServiceException
     */
    public synchronized Object getSingle(boolean createIfNull) throws ServiceException {
        if (single == null && createIfNull) {
            single = createSingle();
        }
        return single;

    }

    private synchronized Object createSingle() throws ServiceException {
        Object single = null;
        try {
            Class<?> clazz = getClassLoader().loadClass(getClazz());
            Constructor<?> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            single = c.newInstance();
            // start
            invokeServiceMethod(single, Start.class);
        } catch (Exception e) {
            throw new ServiceException(e.getCause());
        }
        return single;
    }

    /**
     * Invoke service start/stop
     * 
     * TODO optimize method searching
     * 
     * @param svc
     * @throws Exception
     */
    public static void invokeServiceMethod(Object single, Class<? extends Annotation> anno) throws Exception {
        Class<?> clazz = single.getClass();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(anno)) {
                m.setAccessible(true);
                m.invoke(single);
                break;
            }
        }
    }

    public static Service newInstance(jframe.core.plugin.annotation.Service anno) throws ServiceException {
        if (anno == null) { throw new ServiceException("Service Annotation is null"); }
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
