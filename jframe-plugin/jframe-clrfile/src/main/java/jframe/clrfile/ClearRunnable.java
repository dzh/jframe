/**
 * 
 */
package jframe.clrfile;

import java.io.File;
import java.util.Date;

/**
 * @author dzh
 * @date Jan 16, 2014 10:13:28 AM
 * @since 1.0
 */
public class ClearRunnable implements Runnable {

	private String dest;
	private int expire; // m

	public ClearRunnable(String dest, int expire) {
		this.dest = dest;
		this.expire = expire;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			File f = new File(dest);
			if (!f.exists())
				return;
			File[] files = f.listFiles();
			Date now = new Date();
			long expireTime = expire * 60000;
			for (File file : files) {
				Date d = new Date(file.lastModified());
				if ((now.getTime() - d.getTime()) >= expireTime) {
					file.delete();
				}
			}
		} catch (Exception e) {
		}
	}

}
