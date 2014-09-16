/**
 * 
 */
package jframe.core.plugin.loader.ext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import jframe.core.plugin.Plugin;
import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.loader.PluginCase;
import jframe.core.plugin.loader.PluginClassLoader;
import jframe.core.plugin.service.Service;
import jframe.core.plugin.service.ServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 15, 2014 4:48:41 PM
 * @since 1.1
 */
public class PluginServiceClassLoader extends PluginClassLoader {

	private static final Logger LOG = LoggerFactory
			.getLogger(PluginServiceClassLoader.class);

	private PluginLoaderContext plc;

	public PluginServiceClassLoader(PluginCase pc, PluginLoaderContext plc) {
		super(pc);
		this.plc = plc;
	}

	@Override
	protected Class<?> loadLocalPlugin(String name)
			throws ClassNotFoundException {
		try {
			// TODO Import-Service有点多余, 除了性能之外
			if (getPluginCase().getImportService().contains(name)) {
				return loadOtherPlugin(name);
			}
		} catch (ClassNotFoundException e) {

		}

		Class<?> c = null;
		try {
			// load from plug-in
			c = findClass(name);
			injectService(c);
		} catch (ClassNotFoundException e) {
			c = getParent().loadClass(name);
		}
		return c;
	}

	private void injectService(Class<?> clazz) {
		if (clazz == null)
			return;
		ServiceContext context = plc.getServiceContext();
		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())
					&& f.isAnnotationPresent(InjectService.class)) {
				context.attachService(context.getSvcById(f.getAnnotation(
						InjectService.class).id()), clazz, true);
			}
		}
	}

	public void injectPlugin(Class<? extends Plugin> plugin) {
		injectService(plugin);
	}

	/**
	 * load class from other plug-in
	 * 
	 * @param name
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class<?> loadOtherPlugin(String name) throws ClassNotFoundException {
		Service svc = plc.getServiceContext().getSvcByName(name);
		if (svc != null) {
			return svc.getClassLoader().loadClass(name);
		}
		return null;
	}

	/**
	 * Register service after creating plug-in successfully
	 */
	@Override
	protected Plugin createPlugin(PluginCase pc) {
		Plugin p = super.createPlugin(pc);
		if (p != null) {
			regService(pc, p);
		}
		return p;
	}

	/**
	 * register service meta-data
	 * 
	 * @param pc
	 * @param p
	 */
	private void regService(PluginCase pc, Plugin p) {
		ServiceContext sc = plc.getServiceContext();

		List<String> exportService = pc.getExportService();
		for (String name : exportService) {
			try {
				sc.regSvc(Service
						.newInstance(
								loadClass(name)
										.getAnnotation(
												jframe.core.plugin.annotation.Service.class))
						.setName(name).setClassLoader(this).setPlugin(p));
			} catch (Exception e) {
				LOG.error("Create Annotation Service Error: " + e.getMessage());
				continue;
			}
		}
	}

	public void dispose() {
		detachExportService();
		super.dispose();
	}

	private void detachExportService() {
		ServiceContext context = plc.getServiceContext();
		List<String> exportService = getPluginCase().getExportService();

		for (String svc : exportService) {
			Service s = context.getSvcByName(svc);
			if (s == null)
				continue;
			context.detachService(s.getId());
			context.unregSvc(s.getId());
		}
	}
}
