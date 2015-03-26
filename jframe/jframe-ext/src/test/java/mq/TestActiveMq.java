/**
 * 
 */
package mq;

import java.util.Date;

import jframe.core.msg.TextMsg;

import org.junit.Test;

/**
 * @author dzh
 * @date Mar 24, 2015 10:44:06 AM
 * @since 1.0
 */
public class TestActiveMq {
	public static int SUM = 10000;

	@Test
	public void test() {
		ActivemqDispatcher ad = new ActivemqDispatcher();
		ad.addDispatchTarget(new TestDispatchTarget());
		ad.start();

		System.out.println("Start send msg " + new Date().toString());
		for (int i = 0; i < SUM; i++) {
			ad.receive(new TextMsg()
					.setType(i)
					.setValue(
							"The percentage of range of collision avoidance if enabled"));
		}

		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
		}
		ad.close();
	}
}
