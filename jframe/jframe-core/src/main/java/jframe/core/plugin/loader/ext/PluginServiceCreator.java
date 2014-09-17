package jframe.core.plugin.loader.ext;

import java.util.Properties;

import jframe.core.conf.Config;
import jframe.core.plugin.loader.PluginCase;
import jframe.core.plugin.loader.PluginClassLoader;
import jframe.core.plugin.loader.PluginCreator;

/**
 * 
 * @author dzh
 * @date Sep 15, 2014 4:37:52 PM
 * @since 1.1
 */
public class PluginServiceCreator extends PluginCreator {

	public PluginServiceCreator(Config config) {
		super(config);
	}

	@Override
	protected void loadPlugin(PluginCase pc, Properties p) {
		if (p.getProperty(PluginCase.P_EXPORT_SERVICE) != null) {
			pc.setExportService(parseList(p
					.getProperty(PluginCase.P_EXPORT_SERVICE)));
		}

		if (p.getProperty(PluginCase.P_IMPORT_SERVICE) != null) {
			pc.setImportService(parseList(p
					.getProperty(PluginCase.P_IMPORT_SERVICE)));
		}
	}

	@Override
	public void close() {
		super.close();
	}

	@Override
	public PluginClassLoader createPluginClassLoader(PluginCase pc) {
		return new PluginServiceClassLoader(pc, context);
	}

}
