/**
 * 
 */
package jframe.example.plugin;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Sep 15, 2014 2:53:27 PM
 * @since 1.0
 */
@Service(clazz = "jframe.example.plugin.CountServiceImpl", id = "example.CountService")
public interface CountService {
	int add(int x, int y);

}
