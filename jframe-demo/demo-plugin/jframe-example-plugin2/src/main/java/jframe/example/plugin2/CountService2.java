/**
 * 
 */
package jframe.example.plugin2;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Sep 16, 2014 5:07:48 PM
 * @since 1.0
 */
@Service(id = "jframe.example.CountService2", clazz = "jframe.example.plugin2.CountService2Impl")
public interface CountService2 {

	int mul(int x, int y);

}
