/**
 * 
 */
package mq;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import jframe.core.dispatch.DispatchTarget;
import jframe.core.msg.Msg;

/**
 * @author dzh
 * @date Mar 24, 2015 1:00:47 PM
 * @since 1.0
 */
public class TestDispatchTarget implements DispatchTarget {

	AtomicInteger count = new AtomicInteger(0);

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.DispatchTarget#receive(jframe.core.msg.Msg)
	 */
	public void receive(Msg<?> msg) {
		int c = count.incrementAndGet();
		if (TestActiveMq.SUM - c < 5) {
			System.out.println("Finish recv type->" + msg.getType()
					+ ", data->" + new Date().toString());
			System.out.println("Total msg -> " + c);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.dispatch.DispatchTarget#interestMsg(jframe.core.msg.Msg)
	 */
	public boolean interestMsg(Msg<?> msg) {
		return true;
	}

}
