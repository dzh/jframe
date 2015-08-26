/**
 * 
 */
package jframe.clrfile;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginRecver;

/**
 * 
 * @author dzh
 * @date Jan 16, 2014 9:35:20 AM
 * @since 1.0
 */
public class ClrFilePlugin extends PluginRecver {

	ScheduledExecutorService ses;

	public void start() throws PluginException {
		super.start();
		createScheduler();
	}

	/**
	 * 
	 */
	private void createScheduler() {
		String[] dirs = getConfig("clrfile.dirs", "").split(" ");
		String default_val = getConfig("clrfile.expire", "720");
		ses = Executors.newScheduledThreadPool(2);
		for (int i = 0; i < dirs.length; ++i) {
			int val = 720;
			try {
				val = Integer.parseInt(getConfig("clrfile.expire" + i,
						default_val));
			} catch (Exception e) {
			}
			ses.scheduleAtFixedRate(new ClearRunnable(dirs[i], val), 0, val,
					TimeUnit.MINUTES);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.Plugin#stop()
	 */
	public void stop() throws PluginException {
		try {
			ses.shutdownNow();
			ses.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		super.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRecver#doRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected void doRecvMsg(Msg<?> msg) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginRecver#canRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected boolean canRecvMsg(Msg<?> msg) {
		return false;
	}

}
