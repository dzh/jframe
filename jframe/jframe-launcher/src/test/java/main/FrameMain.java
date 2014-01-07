/**
 * 
 */
package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author dzh
 * @date Oct 10, 2013 3:12:13 PM
 * @since 1.0
 */
public class FrameMain {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException,
			IOException {
		System.out.println("Frame main start");
		for (String arg : args) {
			System.out.println(arg);
		}

		Date s = new Date();
		while (true) {
			Thread.sleep(2000);

			Date d = new Date();
			File f = new File("/home/dzh/temp/test/process.txt");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(d.toString().getBytes());
			fos.close();

			System.out.println("write date: " + d.toString());
			// System.out.flush();
			// System.out.close();

			if (d.getTime() - s.getTime() > 10000) {
				break;
			}
		}
	}

}
