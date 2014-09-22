/**
 * 
 */
package jframe.core.plugin.loader;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * <li>One PluginCase corresponds to One plug-in jar.</li>
 * <li>PluginCase record meta-data about plugin</li>
 * </p>
 * 
 * @author dzh
 * @date Sep 26, 2013 6:01:23 PM
 * @since 1.0
 */
public class PluginCase {

	public static final String LIB = "lib";
	public static final String DLL = "dll";
	public static final String INF = "META-INF";
	public static final String P_PLUGIN_CLASS = "Plugin-Class";
	public static final String P_PLUGIN_NAME = "Plugin-Name";
	public static final String P_PLUGIN_LIB = "Plugin-Lib";
	public static final String P_PLUGIN_DLL = "Plugin-Dll";
	public static final String P_PLUGIN_SERVICE = "Plugin-Service";
	public static final String P_IMPORT_PLUGIN = "Import-Plugin";
	public static final String P_IMPORT_CLASS = "Import-Class";
	public static final String P_EXPORT_CLASS = "Export-Class";

	private String pluginClass;

	private String pluginName;

	private int pluginID;

	private List<String> pluginDll = Collections.emptyList();

	private List<String> pluginLib = Collections.emptyList();

	private List<String> pluginService = Collections.emptyList();

	private List<String> importPlugin = Collections.emptyList();

	private List<String> importClass = Collections.emptyList();

	private List<String> exportClass = Collections.emptyList();

	private String jarPath;

	private String cachePath;

	public List<String> getExportClass() {
		return exportClass;
	}

	public void setExportClass(List<String> exportClass) {
		this.exportClass = exportClass;
	}

	public String getPluginClass() {
		return pluginClass;
	}

	public void setPluginClass(String pluginClass) {
		this.pluginClass = pluginClass;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public int getPluginID() {
		return pluginID;
	}

	public void setPluginID(int pluginID) {
		this.pluginID = pluginID;
	}

	public List<String> getPluginDll() {
		return pluginDll;
	}

	public void setPluginDll(List<String> pluginDll) {
		this.pluginDll = pluginDll;
	}

	public List<String> getPluginLib() {
		return pluginLib;
	}

	public void setPluginLib(List<String> pluginLib) {
		this.pluginLib = pluginLib;
	}

	public String getJarPath() {
		return jarPath;
	}

	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}

	public String getCachePath() {
		return cachePath;
	}

	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}

	public String getCacheLibPath() {
		return this.cachePath + File.separator + LIB;
	}

	public String getCacheDllPath() {
		return this.cachePath + File.separator + DLL;
	}

	public String toString() {
		return "PluginCase: " + "PluginName-" + getPluginName() + ", JarPath-"
				+ getJarPath();
	}

	public List<String> getPluginService() {
		return pluginService;
	}

	public void setPluginService(List<String> pluginService) {
		this.pluginService = pluginService;
	}

	public List<String> getImportPlugin() {
		return importPlugin;
	}

	public void setImportPlugin(List<String> importPlugin) {
		this.importPlugin = importPlugin;
	}

	public List<String> getImportClass() {
		return importClass;
	}

	public void setImportClass(List<String> importClass) {
		this.importClass = importClass;
	}

}
