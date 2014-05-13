/**
 * 
 */
package jframe.swt.ui;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import jframe.core.msg.Msg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author dzh
 * @date Dec 17, 2013 2:55:03 PM
 * @since 1.0
 */
public class JframeApp {

	private Queue<Msg<?>> queue = new LinkedBlockingQueue<Msg<?>>();

	private Shell shell;

	private final CountDownLatch latch = new CountDownLatch(1);
	volatile boolean disposed = false;

	public JframeApp(Display display) {
		this(display, SWT.SHELL_TRIM);
	}

	public JframeApp(Display display, int style) {
		shell = new Shell(display, style);
		configApp();
	}

	protected void configApp() {
		shell.setMaximized(true);
		shell.setText("Jframe App");
		// shell.setImage(new Image());
		shell.setLayout(new GridLayout(1, true));
		shell.setMenuBar(createMenuBar());
		// createToolBar();
		createContent();
		// shell.layout();
	}

	/**
	 * 
	 */
	protected void createContent() {
		Composite content = new Composite(shell, SWT.NONE);
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		content.setLayout(new FillLayout());
		// createTextDemo(content);
		createTabFolder(content);
	}

	/**
	 * @param content
	 */
	private void createTabFolder(Composite content) {
		CTabFolder folder = new CTabFolder(content, SWT.BORDER | SWT.BOTTOM);
		// folder.setSimple(false);
		// folder.setUnselectedImageVisible(false);
		// folder.setUnselectedCloseVisible(false);
		// folder.setMinimizeVisible(true);
		// folder.setMaximizeVisible(true);
		// configuration
		CTabItem startTab = new CTabItem(folder, SWT.NONE);
		startTab.setText("TAB1");
		Composite config = createMonitorConfig(folder);
		startTab.setControl(config);

		// monitor info
		CTabItem item = new CTabItem(folder, SWT.NONE);
		item.setText("TAB2");
		Text text = new Text(folder, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		text.setEditable(false);
		showRecvMsg(text);
		item.setControl(text);

		folder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				// if (event.item.equals(specialItem)) {
				// event.doit = false;
				// }
			}
		});
		folder.setSelection(item);

	}

	/**
	 * @param config
	 */
	private Composite createMonitorConfig(Composite folder) {
		Composite config = new Composite(folder, SWT.NONE);
		config.setLayout(new GridLayout(1, true));

		return config;
	}

	/**
	 * @param text
	 */
	private void showRecvMsg(final Text text) {
		new Thread("MonitorMsg") {
			public void run() {
				while (true) {
					shell.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (!text.isDisposed()) {
								Msg<?> msg = queue.poll();
								if (msg != null) {
									if (text.getText().equals(""))
										text.setText(msg.toString());
									else
										text.setText(text.getText()
												+ text.getLineDelimiter()
												+ msg.toString());
									text.setSelection(text.getCharCount());
									text.showSelection();
								}
							}
						}
					});
					if (isDisposed() && queue.isEmpty()) {
						latch.countDown();
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();

	}

	/**
	 * @param content
	 */
	private void createTextDemo(Composite content) {
		final Text text = new Text(content, SWT.MULTI | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		text.setEditable(false);

		new Thread("HandleMsg") {
			public void run() {
				while (true) {
					shell.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (!text.isDisposed()) {
								Msg<?> msg = queue.poll();
								if (msg != null) {
									if (text.getText().equals(""))
										text.setText(msg.toString());
									else
										text.setText(text.getText()
												+ text.getLineDelimiter()
												+ msg.toString());
								}
							}
						}
					});
					if (isDisposed() && queue.isEmpty()) {
						latch.countDown();
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();

	}

	public void recvMsg(Msg<?> msg) {
		queue.add(msg);
	}

	/**
	 * 
	 */
	protected void createToolBar() {
		CoolBar bar = new CoolBar(shell, SWT.FLAT);
		bar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		bar.setLayout(new RowLayout());

		CoolItem item = new CoolItem(bar, SWT.NONE);
		Button button = new Button(bar, SWT.FLAT);
		// button.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		button.setText("Button");
		Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		item.setPreferredSize(item.computeSize(size.x, size.y));
		item.setControl(button);

		Rectangle clientArea = shell.getClientArea();
		bar.setLocation(clientArea.x, clientArea.y);
		bar.pack();
	}

	/**
	 * @return
	 */
	protected Menu createMenuBar() {
		Menu bar = new Menu(shell, SWT.BAR);
		// file
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
		fileItem.setText("&File");
		Menu submenu = new Menu(shell, SWT.DROP_DOWN);
		fileItem.setMenu(submenu);
		MenuItem item = new MenuItem(submenu, SWT.PUSH);
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

			}
		});
		item.setText("Select &All\tCtrl+A");
		item.setAccelerator(SWT.MOD1 + 'A');
		// edit
		MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
		editItem.setText("&Edit");
		// search
		MenuItem searchItem = new MenuItem(bar, SWT.CASCADE);
		searchItem.setText("&Search");
		return bar;
	}

	public Shell getShell() {
		return shell;
	}

	public void layout2Center() {
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	public void dispose() {
		setDisposed(true);
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		shell.close();
	}

	public boolean isDisposed() {
		return disposed;
	}

	public void setDisposed(boolean disposed) {
		this.disposed = disposed;
	}
}
