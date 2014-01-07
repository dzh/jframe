/**
 * 
 */
package util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.jar.JarFile;

import jframe.core.util.FileUtil;
import jframe.core.util.MathUtil;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author dzh
 * @date Sep 29, 2013 5:44:56 PM
 * @since 1.0
 */
public class TestFileUtil {

	public void testDeleteALl() {
		String path = "/home/dzh/temp/d";
		FileUtil.deleteAll(path);
	}

	public void newDir() {
		String path = "/home/dzh/temp/dddd";
		File file = new File(path);
		file.mkdirs();
	}

	public void testCopyJar() {
		String jar = "/home/dzh/temp/jframe-example-plugin-1.0.0.jar";
		JarFile jf = null;
		try {
			jf = new JarFile(jar);
			FileUtil.copyJarEntry(jf, "META-INF/MANIFEST.MF",
					"/home/dzh/temp/jar/MANIFEST.MF", true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (jf != null)
				try {
					jf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * getName("a/b/c.jar") -> c.jar getName("a/b/c/") -> c getName("/a") -> a
	 * getName("a") -> a
	 */
	// @Test
	public void testPath() {
		Assert.assertEquals("c.jar", FileUtil.getName("a/b/c.jar"));
		Assert.assertEquals("c", FileUtil.getName("a/b/c/"));
		Assert.assertEquals("a", FileUtil.getName("/a"));
		Assert.assertEquals("a", FileUtil.getName("a"));
	}

	@Test
	public void testVersion() {
		String f1 = "jframe-core-1.1.0.jar";
		String f2 = "jframe-core-1.0.01.jar";
		int v = FileUtil.compareVersion(f1, f2);
		System.out.println(v);
	}

	@Test
	public void testChechSum() throws UnsupportedEncodingException {
		byte[] bytes = "你好吗，我很好".getBytes("utf-8");
		System.out.println("bytes's lengtht is " + bytes.length);
		int cs = MathUtil.calcCheckSum(bytes, 1);
		System.out.println(cs);
		cs = MathUtil.calcCheckSum(bytes, 1);
		System.out.println(cs);
		cs = MathUtil.calcCheckSum(bytes, 8);
		System.out.println(cs);
		cs = MathUtil.calcCheckSum(bytes, 8);
		System.out.println(cs);
		cs = MathUtil.calcCheckSum(bytes, 100);
		System.out.println(cs);
		cs = MathUtil.calcCheckSum(bytes, 101);
		System.out.println(cs);
		cs = MathUtil.calcCheckSum(bytes, 200);
		System.out.println(cs);
		cs = MathUtil.calcCheckSum(bytes, 200);
		System.out.println(cs);
	}
}
