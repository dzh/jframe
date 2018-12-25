/**
 * 
 */
package jframe.launcher;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.launcher.api.Config;
import jframe.launcher.api.Launcher;
import jframe.launcher.api.LauncherException;

/**
 * 
 * @author dzh
 * @date Oct 10, 2013 4:28:16 PM
 * @since 1.0
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    /**
     * 
     * @param lclazz
     *            launcher class
     * @param deflclazz
     *            default launcher class
     * @return
     */
    public static Launcher createLauncher(String lclazz) throws LauncherException {
        if (lclazz == null || "".equals(lclazz)) { throw new LauncherException("Not found launcher class" + lclazz); }
        try {
            return (Launcher) Thread.currentThread().getContextClassLoader().loadClass(lclazz).newInstance();
        } catch (Exception e) {
            err(e);
        }
        return null;
    }

    static void err(Exception e) {
        LOG.error(e.getMessage(), e.getCause());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        start(System.getProperty(Config.LAUNCHER));
    }

    static void start(String launcher) {
        Launcher l = null;
        try {
            String appHome = System.getProperty(Config.APP_HOME);
            l = createLauncher(launcher);
            LOG.info("{} is starting from {}!", l.name(), appHome);

            // String fileConfig =
            // System.getProperty(Config.FILE_CONFIG, appHome + File.separator + "conf" + File.separator +
            // Config.FILE_CONFIG_NAME);
            String fileConfig =
                    System.getProperty(Config.FILE_CONFIG, String.join(File.separator, appHome, "conf", Config.FILE_CONFIG_NAME));
            Config config = l.load(fileConfig);
            l.launch(config);
        } catch (Exception e) {
            LOG.error("launcher exception:", e.getCause());
        } finally {
            if (l != null) {
                l.exit(0);
                LOG.info("{} stopped!", l.name());
            }
        }
    }

}
