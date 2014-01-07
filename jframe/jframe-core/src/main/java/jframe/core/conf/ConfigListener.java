/**
 * 
 */
package jframe.core.conf;

import java.util.EventListener;

/**
 * unused
 * <p>
 * 事件监听导致的代码耦合不合适，使用Unit 
 * </p>
 * 
 * @author dzh
 * @date Sep 23, 2013 1:26:50 PM
 * @since 1.0
 */
public interface ConfigListener extends EventListener {

	void modify(String key, String oldVal, String newVal);

}
