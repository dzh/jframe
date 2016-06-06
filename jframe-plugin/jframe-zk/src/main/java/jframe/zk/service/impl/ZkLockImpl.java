/**
 * 
 */
package jframe.zk.service.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import jframe.zk.service.ZkLock;

/**
 * @author dzh
 * @date May 27, 2016 3:58:32 PM
 * @since 1.0
 */
public class ZkLockImpl implements ZkLock {

    /* (non-Javadoc)
     * @see java.util.concurrent.locks.Lock#lock()
     */
    @Override
    public void lock() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.util.concurrent.locks.Lock#lockInterruptibly()
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.util.concurrent.locks.Lock#tryLock()
     */
    @Override
    public boolean tryLock() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.locks.Lock#tryLock(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.locks.Lock#unlock()
     */
    @Override
    public void unlock() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.util.concurrent.locks.Lock#newCondition()
     */
    @Override
    public Condition newCondition() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.locks.ReadWriteLock#readLock()
     */
    @Override
    public Lock readLock() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.locks.ReadWriteLock#writeLock()
     */
    @Override
    public Lock writeLock() {
        // TODO Auto-generated method stub
        return null;
    }

}
