/**
 * 
 */
package jframe.swt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * @author dzh
 * @date Dec 27, 2013 2:07:03 PM
 * @since 1.0
 */
public class ProcessDialog extends Dialog {

	private ProgressBar progressBar;
	private Shell shell;
	private String initText = "";

	/**
	 * @param parent
	 */
	public ProcessDialog(Shell parent) {
		super(parent, SWT.SMOOTH);
	}

	public ProcessDialog(Shell parent, int style) {
		super(parent, style);
		checkSubclass();
	}

	/**
	 * APPLICATION_MODAL, PRIMARY_MODAL, SYSTEM_MODAL, SHEET
	 * 
	 * @param style
	 */
	public void open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.APPLICATION_MODAL);
		shell.setSize(200, 30); // TODO 根据文字判断合适的大小
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		shell.setLayout(new FillLayout());
		Rectangle bounds = parent.getDisplay().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		progressBar = new ProgressBar(shell, getStyle());
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// String string = initText
				// + (progressBar.getSelection()
				// * 1.0
				// / (progressBar.getMaximum() - progressBar
				// .getMinimum()) * 100) + "%";
				String string = initText + " " + progressBar.getSelection()
						+ "%";
				Point point = progressBar.getSize();

				FontMetrics fontMetrics = e.gc.getFontMetrics();
				// int width = fontMetrics.getAverageCharWidth() *
				// string.length();
				int width = e.gc.stringExtent(string).x;
				int height = fontMetrics.getHeight();
				e.gc.setForeground(shell.getDisplay().getSystemColor(
						SWT.COLOR_DARK_GRAY));
				e.gc.drawString(string, (point.x - width) / 2,
						(point.y - height) / 2, true);
			}
		});

		shell.open();
	}

	// public void setMaximum(int value) {
	// progressBar.setMaximum(value);
	// }
	// public void setMinimum(int value) {
	// progressBar.setMinimum(value);
	// }

	public void setSelection(int value) {
		progressBar.setSelection(value);
	}

	public void setText(String text) {
		this.initText = text;
	}

	public int getSelection() {
		return progressBar.getSelection();
	}

	public void setState(int state) {
		progressBar.setState(state);
	}

	public void close() {
		shell.setVisible(false);
		if (!shell.isDisposed())
			shell.close();
		if (!getParent().isDisposed()) {
			getParent().setActive();
		}
	}

}
