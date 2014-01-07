/**
 * 
 */
package jframe.core.plugin;

/**
 * <p>
 * 插件引用
 * <li></li>
 * <li></li>
 * </p>
 * TODO timeout
 * 
 * @author dzh
 * @date Sep 28, 2013 4:03:44 PM
 * @since 1.0
 */
public interface PluginRef {

	String DispatchSource = "policy.dispatchsource";

	String DispatchTarget = "policy.dispatchtarget";

	PluginContext getContext();

	Plugin getPlugin();

	void setPlugin(Plugin plugin);

	String getPluginName();

	boolean isUpdating();

	void setUpdating(boolean u);

	void regPolicy(String name, Object policy);

	void unregPolicy(String name);

	Object getPolicy(String name);

	/**
	 * dispose resource at last
	 */
	void dispose();
}
