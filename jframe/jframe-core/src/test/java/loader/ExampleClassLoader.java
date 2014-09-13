/**
 * 
 */
package loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author dzh
 * @date Sep 11, 2013 4:57:15 PM
 * @since 1.0
 */
public class ExampleClassLoader extends URLClassLoader {

	/**
	 * @param urls
	 */
	public ExampleClassLoader(URL[] urls) {
		super(urls);
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IOException {
		CustomClassLoader ccl = new CustomClassLoader();
		Class cl = ccl.loadClass("jframe.example.plugin.Test");
		System.out.println("CustomClassLoader -->"
				+ cl.newInstance().toString());

		URL url1 = new URL("file:"
				+ "/home/dzh/temp/jframe-example-plugin-1.0.0/");
		URLClassLoader loader = URLClassLoader.newInstance(new URL[] { url1 });
		while (true) {
			Class clazz = Class.forName("jframe.example.plugin.Test", false,
					loader);
			Object t = clazz.newInstance();
			System.out.println(t.toString());
			loader.close();
			t = clazz.newInstance();
			System.out.println(t.toString());
			// clazz = Class.forName("jframe.example.plugin.ExamplePlugin",
			// true,
			// loader);
			// Object p = clazz.newInstance();
			// System.out.println(p.toString());

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}

		}

		loader.close();
	}

}
