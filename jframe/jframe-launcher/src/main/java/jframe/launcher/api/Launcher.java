/**
 * 
 */
package jframe.launcher.api;

/**
 * @author dzh
 * @date Sep 23, 2013 10:54:33 AM
 * @since 1.0
 */
public interface Launcher {

    /**
     * read and parse config file
     * 
     * @param file
     * @return
     * @throws LauncherException
     */
    Config load(String file) throws LauncherException;

    /**
     * 
     * @param config
     * @throws Exception
     */
    void launch(Config config) throws LauncherException;

    /**
     * invoked when progress exit
     * 
     * @param status
     *            Program exist status, zero is normal
     */
    void exit(int status);

    /**
     * launcher name
     * 
     * @return
     */
    String name();

}
