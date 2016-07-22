/**
 * 
 */
package util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.JarFile;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import jframe.core.util.FileUtil;
import jframe.core.util.MathUtil;

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
            FileUtil.copyJarEntry(jf, "META-INF/MANIFEST.MF", "/home/dzh/temp/jar/MANIFEST.MF", true);
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
    @Test
    public void testPath() {
        Assert.assertEquals("c.jar", FileUtil.getLastName("a/b/c.jar"));
        Assert.assertEquals("c", FileUtil.getLastName("a/b/c/"));
        Assert.assertEquals("a", FileUtil.getLastName("/a"));
        Assert.assertEquals("a", FileUtil.getLastName("a"));
    }

    @Test
    public void testVersion() {
        String f1 = "jframe-core-1.1.0.jar";
        String f2 = "jframe-core-1.0.01.jar";
        int v = FileUtil.compareVersion(f1, f2);
        System.out.println(v);
    }

    @Test
    @Ignore
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

    public void testSysprops() {
        System.out.println("------------------");
        Enumeration<?> names = System.getProperties().propertyNames();
        while (names.hasMoreElements()) {
            String key = String.valueOf(names.nextElement());
            System.out.println(key + "-" + System.getProperty(key));
        }
    }

    public void testEnv() {
        System.out.println("------------------");
        for (Entry<String, String> e : System.getenv().entrySet()) {
            System.out.println(e.getKey() + "-" + e.getValue());
        }
    }

    @Test
    @Ignore
    public void testRemove() {
        List<Object> list = new LinkedList<Object>();
        Object one = new Object();
        Object two = new Object();
        list.add(one);
        list.add(two);
        try {
            for (Object s : list) {
                list.remove(s);
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.add(one);
        list.add(two);
        Iterator<?> iter = list.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            iter.remove();
            System.out.println(o);
        }

        list.add(one);
        list.add(two);
        for (Object o : list.toArray()) {
            list.remove(o);
            System.out.println(o);
        }
    }

}
