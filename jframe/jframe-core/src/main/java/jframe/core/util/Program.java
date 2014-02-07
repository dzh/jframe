/**
 * 
 */
package jframe.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * @author dzh
 * @date Jun 9, 2013 2:14:50 PM
 */
public class Program {

	/**
	 * @return the PID of current Java process
	 */
	public static final String getPID() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return name.substring(0, name.indexOf('@'));
	}

	/**
	 * 
	 * @param pid
	 *            进程ID
	 * @param pid_file
	 *            写入文件
	 * @throws IOException
	 */
	public static void writePID(String pid, String pid_file) throws IOException {
		File file = new File(pid_file);
		writePID(pid, file);
	}

	public static void writePID(String pid, File pid_file) throws IOException {
		if (!pid_file.exists()) {
			pid_file.getParentFile().mkdirs();
			if (!pid_file.createNewFile())
				throw new IOException("Can't create the pid file: "
						+ pid_file.getAbsolutePath());
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(pid_file);
			fw.write(pid);
			fw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fw != null)
				fw.close();
		}
	}

}
