/**
 * 
 */
package jframe.launcher.api;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import jframe.launcher.util.VarProperties;

/**
 * @author dzh
 * @date Feb 15, 2016 11:57:40 AM
 * @since 1.0
 */
public abstract class DefLauncher implements Launcher {

    protected Config _config = null;

    /*
     * (non-Javadoc)
     * 
     * @see jframe.launcher.api.Launcher#load(java.lang.String)
     */
    public Config load(String file) throws LauncherException {
        File configFile = new File(file);
        assert configFile.exists() : "Not found config.properties";

        Config config = null;
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows") && System.getProperties().containsKey(Config.APP_HOME)) {
                System.setProperty(Config.APP_HOME, System.getProperty(Config.APP_HOME).replaceAll("\\\\", "/"));
            }
            VarProperties props = new VarProperties(System.getProperties());
            props.load(file);
            config = new DefConfig(props);
            config.setConfig(Config.APP_HOME, props.getProperty(Config.APP_HOME));
            config.setConfig(Config.LAUNCHER, props.getProperty(Config.LAUNCHER));

            // TODO verify variables in config.properties
            _config = config;
            return config;
        } catch (Exception e) {
            throw new LauncherException(e.getMessage());
        }
    }

    protected String config(String k, String defv) {
        return _config.getConfig(k, defv);
    }

    private static final void stopLog() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();
    }

    public void exit(int status) {
        _config = null;
        stopLog();
        System.exit(status);
    }

    public String name() {
        return getClass().getName();
    }

}
