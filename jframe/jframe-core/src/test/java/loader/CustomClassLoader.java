/**
 * 
 */
package loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author dzh
 * @date Sep 26, 2013 5:24:40 PM
 * @since 1.0
 */
public class CustomClassLoader extends ClassLoader {

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		URL url = null;
		try {
			url = new URL("file:/home/dzh/temp/jframe-example-plugin-1.0.0/"
					+ name.replace(".", "/").concat(".class"));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream is = null; 
		try {
			is = url.openStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[64];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}

			return defineClass(name, baos.toByteArray(), 0, baos.size());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
		return null;
	}
}
