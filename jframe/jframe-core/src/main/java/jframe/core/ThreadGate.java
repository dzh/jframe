/**
 * 
 */
package jframe.core;

public class ThreadGate {

	private boolean isOpen = false;

	public synchronized void open() {
		isOpen = true;
		notifyAll();
	}

	public synchronized void close() {
		isOpen = false;
	}

	public synchronized void await(long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		long remaining = timeout;

		while (!isOpen) {
			wait();
			if (timeout > 0) {
				remaining = timeout - (System.currentTimeMillis() - start);
				if (remaining <= 0) {
					break;
				}
			}
		}
	}

}
