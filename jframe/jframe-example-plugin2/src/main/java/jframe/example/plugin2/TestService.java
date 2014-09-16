/**
 * 
 */
package jframe.example.plugin2;

import jframe.core.plugin.annotation.InjectService;
import jframe.example.plugin.CountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 15, 2014 5:50:53 PM
 * @since 1.0
 */
public class TestService {

	static Logger LOG = LoggerFactory.getLogger(TestService.class);

	@InjectService(id = "example.CountService")
	private static CountService cs;

	public void test() {
		LOG.info(String.valueOf(cs.add(1, 2)));
	}

}
