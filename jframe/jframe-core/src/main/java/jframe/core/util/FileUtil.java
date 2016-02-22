/**
 * 
 */
package jframe.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 29, 2013 5:30:39 PM
 * @since 1.0
 */
public class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    /**
     * use getLastName
     * 
     * @param path
     * @return
     */
    @Deprecated
    public static final String getName(String path) {
        int loc = path.lastIndexOf("/");
        if (loc == -1)
            return path;
        if (loc < path.length() - 1) {
            return path.substring(loc + 1);
        }
        // loc == path.length() - 1
        return getName(path.substring(0, loc));
    }

    /**
     * <pre>
     * intercept path end,for example:
     * getName("a/b/c.jar") -> c.jar
     * getName("a/b/c/") -> c
     * getName("/a") -> a
     * getName("a") -> a
     * </pre>
     * 
     * @param path
     * @return
     */
    public static final String getLastName(String path) {
        int loc = path.lastIndexOf("/");
        if (loc == -1)
            return path;
        if (loc < path.length() - 1) {
            return path.substring(loc + 1);
        }
        // loc == path.length() - 1
        return getLastName(path.substring(0, loc));
    }

    /**
     * 
     * @param file
     *            file's name
     * @return file's suffix if it existed, or empty string
     */
    public static final String getSuffix(String file) {
        if (file == null)
            return "";
        int loc = file.lastIndexOf('.');
        if (loc == -1 || loc == file.length() - 1)
            return "";
        return file.substring(loc + 1);
    }

    /**
     * delete all files and folders under this path
     * 
     * @param path
     */
    public static final void deleteAll(File path) {
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                deleteAll(f);
            }

            if (path.listFiles().length == 0) {
                path.delete();
            }
        } else {
            path.delete();
        }
    }

    public static final void deleteAll(String path) {
        deleteAll(new File(path));
    }

    public static final void write(File to, byte[] content) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(to);
            fos.write(content);
            fos.flush();
        } catch (Exception e) {
            LOG.warn(e.getMessage());
            throw e;
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage());
                }
        }

    }

    public static final void copy(String target, String source, boolean overwriting) throws IOException {
        File sf = new File(source);
        if (!sf.exists())
            throw new IOException("Not found source file: " + sf.getAbsolutePath());

        File tf = new File(target);
        if (overwriting && tf.exists()) {
            tf.delete();
            tf.createNewFile();
        }
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream(tf);
            fis = new FileInputStream(sf);
            byte[] buf = new byte[64];
            int len = -1;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        } finally {
            if (fos != null)
                fos.close();
            if (fis != null)
                fis.close();
        }
    }

    /**
     * 
     * @param jar
     *            source jar file
     * @param jarEntry
     *            source file
     * @param dest
     *            target file path
     */
    public static final void copyJarEntry(JarFile jar, String jarEntry, String dest, boolean covered) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            JarEntry je = jar.getJarEntry(jarEntry);
            if (je == null) {
                LOG.warn("Not found jar entry " + jarEntry);
                return;
            }
            File df = new File(dest);
            if (df.exists()) {
                if (covered) {
                    df.delete();
                } else {
                    return;
                }
            }

            df.getParentFile().mkdirs();
            fos = new FileOutputStream(df);
            is = jar.getInputStream(je);
            byte[] buf = new byte[64];
            int len = -1;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage());
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage());
                }
            }
        }
    }

    /**
     * load file's content to p
     * 
     * @param p
     * @param file
     *            properties file
     */
    public static void loadToProps(Properties p, File file) {
        if (!file.exists())
            return;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            p.load(fis);
        } catch (FileNotFoundException e) {
            LOG.warn(e.getMessage());
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        } finally {
            if (file != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
        }
    }

    public static void loadToProps(Properties p, String file) {
        loadToProps(p, new File(file));
    }

    /**
     * compare file's version
     * 
     * @param f1
     *            jar file eg. *-1.0.0.jar
     * @param f2
     *            jar file
     * @return
     */
    public static int compareVersion(String f1, String f2) {
        String v1 = null, v2 = null;
        int loc = f1.lastIndexOf("-");
        if (loc != -1) {
            v1 = f1.substring(loc + 1);
        }
        loc = f2.lastIndexOf("-");
        if (loc != -1) {
            v2 = f2.substring(loc + 1);
        }
        return v1.compareTo(v2);
    }
}
