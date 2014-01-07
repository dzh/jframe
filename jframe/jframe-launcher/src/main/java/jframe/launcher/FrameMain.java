/**
 * 
 */
package jframe.launcher;

import java.io.File;
import java.io.IOException;

import jframe.core.DefFrameFactory;
import jframe.core.Frame;
import jframe.core.FrameEvent;
import jframe.core.FrameFactory;
import jframe.core.conf.Config;
import jframe.core.conf.ConfigConstants;
import jframe.core.util.ConfigUtil;
import jframe.core.util.Program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

/**
 * @author dzh
 * @date Sep 23, 2013 11:02:46 AM
 * @since 1.0
 */
public class FrameMain {

	private static final Logger LOG = LoggerFactory.getLogger(FrameMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOG.info("FrameMain is starting!");
		String configFile = System.getProperty(ConfigConstants.FILE_CONFIG);
		if (configFile == null || "".equals(configFile)) {
			configFile = System.getProperty(ConfigConstants.APP_HOME)
					+ File.separator + "conf" + File.separator
					+ ConfigConstants.FILE_CONFIG;
		}

		File confFile = new File(configFile);
		assert confFile.exists() : "Not found config.properties";

		final Config config = ConfigUtil.genNewConfig(configFile);
		LOG.info("Loading config.properties successfully!");

		LOG.info("write pid file: " + config.getConfig(ConfigConstants.PID_APP));
		try {
			Program.writePID(Program.getPID(),
					config.getConfig(ConfigConstants.PID_APP));
		} catch (IOException e) {
			LOG.error(e.getMessage());
			config.clearConfig();
			exitSystem(-1);
		}

		FrameFactory ff = getFrameFactory();
		final Frame frame = ff.createFrame(config);
		// TODO

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					if (frame != null) {
						LOG.info("FrameMain delete pid:"
								+ config.getConfig(ConfigConstants.PID_APP));
						new File(config.getConfig(ConfigConstants.PID_APP))
								.deleteOnExit();

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

		LOG.info("Application Stopped Successfully!");
		exitSystem(0);
	}

	/**
	 * @return
	 */
	private static FrameFactory getFrameFactory() {
		return new DefFrameFactory();
	}

	private static final void stopLog() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		loggerContext.stop();
	}

	private static void exitSystem(int status) {
		stopLog();
		System.exit(status);
	}

}
