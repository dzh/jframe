/**
 * 
 */
package jframe.core.conf;

import jframe.launcher.api.LauncherConstants;

/**
 * @author dzh
 * @date Oct 10, 2013 7:36:11 PM
 * @since 1.0
 */
public interface ConfigConstants extends LauncherConstants {

    /********************** Frame ***********************/
    String APP_CONF = "app.conf"; // application configuration directory
    String APP_LIB = "app.lib"; // application core library directory
    String APP_PLUGIN = "app.plugin"; // application plug-in directory
    String APP_UPDATE = "app.update"; // application update directory
    String APP_CACHE = "app.cache"; // application cache directory
    String APP_TEMP = "app.temp"; // application temporary directory
    String FILE_UNITS = "units.xml"; // units file path
    String ARG_CLEAN = "clean"; // clean cache
    String APP_NAME = "app.name";
    String APP_FRAME = "app.frame";
    /*****************************************************/

    // String LAUNCH_MODE_NORMAL = "normal";
    /*****************************************************/

    /********************** PluginContext ***********************/
    String CONTEXT = "context";
    String CONTEXT_DISPATCHER = "context.dispatcher";
    String CONTEXT_DISPATCHER_ID = "context.dispatcher.id";
    /*****************************************************/

    /********************** Plugin ***********************/
    String PLUGIN_FORBID = "plugin.forbid"; // plug-ins not be used(start|reg)
    /*****************************************************/
}
