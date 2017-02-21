/**
 * 
 */
package jframe.core;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.FrameConfig;
import jframe.launcher.api.Config;
import jframe.launcher.api.DefLauncher;
import jframe.launcher.api.LauncherException;
import jframe.launcher.util.Program;

/**
 * @author dzh
 * @date Feb 15, 2016 3:11:38 PM
 * @since 1.0
 */
public class FrameLauncher extends DefLauncher {

    static Logger LOG = LoggerFactory.getLogger(FrameLauncher.class);

    /*
     * (non-Javadoc)
     * 
     * @see jframe.launcher.api.Launcher#launch(jframe.launcher.api.Config)
     */
    public void launch(Config config) throws LauncherException {
        jframe.core.conf.Config frameConfig = convert(config);
        final String pid = frameConfig.getConfig(Config.PID_APP);
        try {
            LOG.info("write pid file: " + pid);
            Program.writePID(Program.getPID(), pid);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            frameConfig.clearConfig();
            exit(-1);
        }

        FrameFactory ff = getFrameFactory();
        final Frame frame = ff.createFrame(frameConfig);

        Runtime.getRuntime().addShutdownHook(new Thread("FrameShutdownhookThread") {
            public void run() {
                try {
                    if (frame != null) {
                        LOG.info("FrameLauncher delete pid {}", pid);
                        new File(pid).deleteOnExit();

                        frame.stop();
                        frame.waitForStop(0);
                    }
                } catch (Exception e) {
                    LOG.error("Shutdown Error:" + e.getMessage());
                }
            }
        });

        FrameEvent event = null;
        do {
            frame.start();
            event = frame.waitForStop(0);
        } while (event.getType() != FrameEvent.Stop);

        LOG.info("FrameLauncher Stopped Successfully!");
        exit(0);
    }

    private jframe.core.conf.Config convert(Config config) {
        jframe.core.conf.Config fc = new FrameConfig();
        for (String k : config.keySet()) {
            fc.setConfig(k, config.getConfig(k));
        }
        return fc;
    }

    /**
     * @return
     */
    private FrameFactory getFrameFactory() {
        return new DefFrameFactory();
    }

}
