/**
 * 
 */
package jframe.core.plugin.loader.ext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.loader.PluginCase;
import jframe.core.plugin.loader.PluginClassLoader;
import jframe.core.plugin.loader.PluginClassLoaderContext;
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

	public PluginServiceClassLoader(PluginCase pc, PluginClassLoaderContext plc) {
		super(pc, plc);
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(PluginServiceClassLoader.class);
	
	@Override
	protected void injectAnnocation(Class<?> clazz) throws Exception {
		if (clazz == null)
			return;
		super.injectAnnocation(clazz);

		Field[] fields = clazz.getDeclaredFields();
		// inject service
		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers())
					&& f.isAnnotationPresent(InjectService.class)) {
				injectImportService(f);
			}
		}
	}

	protected void injectImportService(Field f) {
		ServiceContext context = plc.getServiceContext();
		context.attachService(
				context.getSvcById(f.getAnnotation(InjectService.class).id()),
				f, true);
	}

	/**
	 * register export-service
	 * 
	 * @param pc
	 * @param p
	 */
	public void loadService(PluginCase pc) {
		ServiceContext sc = plc.getServiceContext();

		List<String> pluginService = pc.getPluginService();
		for (String name : pluginService) {
			try {
				sc.regSvc(Service
						.newInstance(
								loadClass(name)
										.getAnnotation(
												jframe.core.plugin.annotation.Service.class))
						.setName(name).setClassLoader(this));
			} catch (Exception e) {
				LOG.error("Create Annotation Service Error: {}", e.getMessage());
				continue;
			}
		}
	}

	public void dispose() {
		detachPluginService();
		super.dispose();
	}

	private void detachPluginService() {
		ServiceContext context = plc.getServiceContext();
		List<String> pluginService = getPluginCase().getPluginService();

		for (String svc : pluginService) {
			Service s = context.getSvcByName(svc);
			if (s == null)
				continue;
			context.detachService(s.getId());
			context.unregSvc(s.getId());
		}
	}
}
