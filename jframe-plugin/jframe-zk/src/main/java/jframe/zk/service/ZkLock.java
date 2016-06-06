/**
 * 
 */
package jframe.zk.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * <p>
 * distributed lock:
 * <li></li>
 * 
 * </p>
 * 
 * @author dzh
 * @date May 27, 2016 3:51:14 PM
 * @since 1.0
 */
public interface ZkLock extends Lock, ReadWriteLock {

}
