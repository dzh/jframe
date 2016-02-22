/**
 * 
 */
package jframe.launcher.api;

import java.util.regex.Pattern;

/**
 * @author dzh
 * @date Feb 16, 2016 5:13:36 PM
 * @since 1.0
 */
public interface LauncherConstants {

    String REGEX_VAR = "\\$\\{(.+?)\\}";
    Pattern P_VAR = Pattern.compile(REGEX_VAR, Pattern.CASE_INSENSITIVE);
    String UTF8 = "utf-8";

    /********************** VAR in shell argument ***********************/
    String APP_HOME = "app.home"; // application root directory
    String FILE_CONFIG = "file.config"; // file path of config.properties
    String LAUNCHER = "launcher"; // launcher class

    /********************** VAR in config.properties ***********************/
    String LAUNCH_MODE = "launch.mode"; // launching mode, daemon or not
    String APP_MAIN = "app.main"; // main class of application
    String APP_LAUNCHER = "app.launcher"; // launcher class of application
    String PID_APP = "app.pid"; // launcher progress's pid file name
    String PID_DAEMON = "daemon.pid"; // main/daemon progress's pid file name
    String FILE_VMARGS = "vmargs";

    /********************** VAR value ***********************/
    String LAUNCH_MODE_DAEMON = "daemon"; //
    String FILE_CONFIG_NAME = "config.properties";

}
