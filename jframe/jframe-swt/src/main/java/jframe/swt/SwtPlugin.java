/**
 * 
 */
package jframe.swt;

import java.util.Random;
import java.util.UUID;

import jframe.core.msg.Msg;
import jframe.core.msg.TextMsg;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginSenderRecver;
import jframe.core.plugin.annotation.Message;
import jframe.swt.ui.JframeApp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author dzh
 * @date Dec 17, 2013 12:34:49 PM
 * @since 1.0
 */
@Message(isSender = true, isRecver = true, recvConfig = true)
public class SwtPlugin extends PluginSenderRecver {

	UIThread uit = null;

	volatile boolean genMsg = true;

	public void start() throws PluginException {
		super.start();
		genMsg = true;
		uit = new UIThread();
		uit.start();
		new Thread("genMsg") {
			public void run() {
				while (genMsg) {
					TextMsg msg = new TextMsg();
					msg.setType(new Random().nextInt(10) % 10 + 1);
					msg.setValue(UUID.randomUUID().toString());
					uit.recvMsg(msg);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						logError(e.getMessage());
					}
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.Plugin#stop()
	 */
	public void stop() throws PluginException {
		genMsg = false;
		uit.exit();
		super.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.Plugin#destroy()
	 */
	public void destroy() throws PluginException {
		uit = null;
		super.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.plugin.PluginSenderRecver#doRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected void doRecvMsg(Msg<?> msg) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jframe.core.plugin.PluginSenderRecver#canRecvMsg(jframe.core.msg.Msg)
	 */
	@Override
	protected boolean canRecvMsg(Msg<?> msg) {
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	class UIThread extends Thread {

		public UIThread() {
			super();
			setName("UIThread");
		}

		JframeApp app;

		public void run() {
			try {
				while (true) {
					Display display = new Display();
					display.addFilter(SWT.Close, new Listener() {
						public void handleEvent(Event event) {
							event.doit = false;
						}
					});
					app = new JframeApp(display, SWT.SHELL_TRIM);
					Shell shell = app.getShell();
					shell.open();

					// shell.pack();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch())
							display.sleep();
					}
					if (!app.isDisposed())
						app.dispose();
					display.dispose();
					break;
				}
			} catch (Exception e) {
				logError(e.getMessage());
			}
		}

		public void recvMsg(Msg<?> msg) {
			if (app != null)
				app.recvMsg(msg);
		}

		public void exit() {
			if (app != null && !app.getShell().isDisposed())
				app.dispose();
		}
	}

}
