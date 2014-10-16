/**
 * 
 */
package jframe.example.plugin2;

import jframe.core.msg.Msg;
import jframe.core.plugin.PluginException;
import jframe.core.plugin.PluginSenderRecver;
import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.example.plugin.CountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 15, 2014 2:42:21 PM
 * @since 1.0
 */
@Injector
public class ExamplePlugin2 extends PluginSenderRecver {

	static final Logger LOG = LoggerFactory.getLogger(ExamplePlugin2.class);

	private TestService test;

	@InjectService(id = "example.CountService")
	private static CountService cs;

	@InjectService(id = "example.CountService2")
	private static CountService2 cs2;

	public void test() {
		if (cs != null)
			LOG.info("ExamplePlugin cs 1 + 2 = {}", cs.add(1, 2));
		if (cs2 != null) {
			LOG.info("ExamplePlugin2 cs 6 * 7 = {}", cs2.mul(6, 7));
		}
	}

	@Override
	public void start() throws PluginException {
		super.start();
		test();

		test = new TestService();
		test.test();
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
