/**
 * 
 */
package jframe.example.plugin2;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginSenderRecver;
import jframe.core.plugin.annotation.InjectService;
import jframe.example.plugin.CountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 15, 2014 2:42:21 PM
 * @since 1.0
 */
public class ExamplePlugin2 extends PluginSenderRecver {

	static final Logger LOG = LoggerFactory.getLogger(ExamplePlugin2.class);

	private TestService test;

	@InjectService(id = "example.CountService")
	private static CountService cs;

	public void test() {
		if (cs != null)
			LOG.info("ExamplePlugin2 cs 1 + 2 = {}", cs.add(1, 2));
	}

	@Override
	public void start() throws PluginException {
		test = new TestService();
		LOG.info("ExamplePlugin2 is start");

		test();
		if (cs != null)
			LOG.info("cs 11 + 22 = {}", cs.add(11, 22));
	}

	@Override
	protected void doRecvMsg(Msg<?> msg) {
		test.test();
	}

	@Override
	protected boolean canRecvMsg(Msg<?> msg) {
		return false;
	}

}
