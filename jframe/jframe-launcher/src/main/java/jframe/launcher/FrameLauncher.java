/**
 * 
 */
package jframe.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 框架启动
 * 
 * @author dzh
 * @date Aug 26, 2013 9:29:07 PM
 * @since 1.0
 */
public class FrameLauncher implements Launcher {

	private static final Logger LOG = LoggerFactory
			.getLogger(FrameLauncher.class);

	private boolean useDaemon = false;

	public FrameLauncher(boolean useDaemon) {
		this.useDaemon = useDaemon;
	}

	public FrameLauncher() {
		this.useDaemon = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.launcher.Lancher#isDaemon()
	 */
	public boolean isDaemon() {
		return useDaemon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.launcher.Launcher#launch()
	 */
	public void launch() {
		LOG.info("Frame is Launching");

		if (isDaemon()) {
			// LOG.info("Starting with daemon mode");

		} else {

		}
	}

}
