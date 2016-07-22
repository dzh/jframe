/**
 * 
 */
package main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Oct 10, 2013 3:11:00 PM
 * @since 1.0
 */
public class TestProcess {

    public static void main(String[] args) throws IOException {
        System.setProperty("test.dzh", "test.dzh.value");

        System.out.println("System properties: ");
        Enumeration<Object> keys = System.getProperties().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            System.out.println("key: " + key.toString() + " --> val: " + System.getProperty(key.toString()));
        }

        System.out.println("Environment: ");
        for (String key : System.getenv().keySet()) {
            System.out.println("key: " + key + " --> val: " + System.getenv(key));
        }

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(Arrays.asList(new String[] { "java", "-cp", "/home/dzh/temp/test/*", "process.FrameMain" }));
        Process p = pb.start();
        InputStream is = p.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[64];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        System.out.println(new String(baos.toByteArray()));
        System.out.println(p.exitValue());

        // Runtime.getRuntime().exec(
        // "java -cp /home/dzh/temp/test/test-java-1.0.0.jar
        // process.FrameMain");
    }

    @Test
    public void testLog() {
        Logger LOG = LoggerFactory.getLogger(TestProcess.class);
        LOG.info("jfkadja{}-{}", 1, 2);
    }

}
