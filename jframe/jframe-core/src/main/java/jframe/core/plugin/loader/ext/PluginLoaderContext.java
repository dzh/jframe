package jframe.core.plugin.loader.ext;

import jframe.core.plugin.service.ServiceContext;

/**
 * <p>
 * <li>service finding</li>
 * <li></li>
 * </p>
 * 
 * @author dzh
 * @date Sep 15, 2014 5:11:00 PM
 * @since 1.1
 */
public class PluginLoaderContext {

	private ServiceContext _sc;

	public PluginLoaderContext() {
		_sc = new ServiceContext();
	}

	public ServiceContext getServiceContext() {
		return _sc;
	}

	public void close() {
		_sc.close();
	}

}
