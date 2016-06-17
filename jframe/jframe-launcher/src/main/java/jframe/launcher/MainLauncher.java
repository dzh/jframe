/**
 * 
 */
package jframe.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.launcher.api.Config;
import jframe.launcher.api.DefConfig;
import jframe.launcher.api.DefLauncher;
import jframe.launcher.util.Program;
import jframe.launcher.util.VmargsFile;

/**
 * <p>
 * daemon运行
 * <li>加载config.properties</li>
 * <li>判断是否daemon执行</li>
 * <li>写PID到daemon.pid</li>
 * <li>配置子进程(app.home)，调用launcher启动子进程，默認launcher是FrameMain</li>
 * <li>监听子进程，若异常停止则重启</li>
 * <li>子进程正常关闭时，自己也关闭</li>
 * </p>
 * <p>
 * normal运行
 * <li>加载config.properties</li>
 * <li>判断是否daemon执行</li>
 * <li>配置子进程(app.home)，调用launcher启动子进程</li>
 * <li>正常终止自己</li>
 * </p>
 * <p>
 * 父子进程关系
 * <li>父进程拷贝所有系统属性给子进程</li>
 * </p>
 * 
 * @author dzh
 * @date Jan 31, 2016 2:16:53 PM
 * @since 1.0
 */
public class MainLauncher extends DefLauncher {

    private static Logger LOG = LoggerFactory.getLogger(MainLauncher.class);

    public void launch(Config config) {
        String daemon = config.getConfig(Config.LAUNCH_MODE);
        if (Config.LAUNCH_MODE_DAEMON.equalsIgnoreCase(daemon)) {
            launchDaemon(config);
        } else {
            launchNormal(config);
        }
    }

    /**
     * @param conf
     */
    private void launchNormal(Config config) {
        LOG.info("Launching normal mode!");
        launchInternal(config);
    }

    private Process launchInternal(Config config) {
        String mainClazz = config.getConfig(Config.APP_MAIN);
        if (mainClazz == null || "".equals(mainClazz)) {
            mainClazz = Main.class.getName(); //
            LOG.warn("app.main is nil, use default main class {}", mainClazz);
        }
        List<String> list = new VmargsFile().loadVmargs(config.getConfig(Config.FILE_VMARGS));
        for (int i = 0; i < list.size(); i++) {
            String arg = list.get(i);
            list.set(i, DefConfig.replace(config, arg));
        }

        String conf = System.getProperty(Config.FILE_CONFIG, config.getConfig(Config.FILE_CONFIG));
        if (conf != null) {
            list.add(0, "-Dfile.config=" + conf);
        }
        list.add(0, "-Dapp.home=" + config.getConfig(Config.APP_HOME));
        list.add(0, "-Dlauncher=" + config.getConfig(Config.APP_LAUNCHER));
        list.add(0, "java");
        list.add(mainClazz);
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(list);
        // pb.redirectErrorStream(true);

        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return p;
    }

    /**
     * @param conf
     */
    private void launchDaemon(final Config config) {
        LOG.info("Launching daemon mode! Write pid file: {}", config.getConfig(DefConfig.PID_DAEMON));

        try {
            Program.writePID(Program.getPID(), config.getConfig(DefConfig.PID_DAEMON));
        } catch (IOException e) {
            LOG.error(e.getMessage());
            exit(-1);
        }

        final String pid_daemon = config.getConfig(Config.PID_DAEMON);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    new File(pid_daemon).deleteOnExit();
                } catch (Exception e) {
                    LOG.error("Shutdown Error:" + e.getMessage());
                }
            }
        });

        int exit = 0;
        do {
            Process subp = launchInternal(config);//
            if (subp == null) {
                LOG.error("Launch MainLauncher Error! Are vmargs set the correct value?(win/linux)");
                break;
            }
            Thread errT = redirectStream(subp.getErrorStream());
            Thread stdT = redirectStream(subp.getInputStream());
            LOG.info("Startup daemon process successfully!");
            try {
                exit = subp.waitFor();
            } catch (Exception e) {
                LOG.warn(e.getMessage());
                break;
            } finally {
                if (errT != null)
                    errT.interrupt();
                if (stdT != null)
                    stdT.interrupt();
            }
            LOG.info("Shutdown daemon process Successfully!");
        } while (exit != 0 && exit != 143);
    }

    /**
     * @param inputStream
     */
    private Thread redirectStream(final InputStream inputStream) {
        if (inputStream == null) {
            LOG.error("Main redirectStream null");
            return null;
        }

        Thread t = new Thread("redirectStream") {
            public void run() {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(
                            new InputStreamReader(inputStream, config(Config.FILE_CONFIG, Config.UTF8)));
                    while (!Thread.interrupted()) {
                        try {
                            String str = br.readLine();
                            if (str == null)
                                Thread.sleep(1000);
                            else
                                LOG.info(str);
                        } catch (IOException e) {
                            LOG.error(e.getMessage());
                            break;
                        }
                    }
                } catch (Exception e) {
                    LOG.warn(e.getMessage());
                } finally {
                    if (br != null)
                        try {
                            br.close();
                        } catch (Exception e) {
                        }
                }
            }
        };
        t.setDaemon(true);
        t.start();
        return t;
    }

}
