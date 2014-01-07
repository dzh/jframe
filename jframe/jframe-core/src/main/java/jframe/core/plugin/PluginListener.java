/**
 * 
 */
package jframe.core.plugin;

import java.util.EventListener;

/**
 * @author dzh
 * @date Sep 12, 2013 9:42:33 PM
 * @since 1.0
 */
public interface PluginListener extends EventListener {

	void pluginChanged(PluginEvent event);

}
