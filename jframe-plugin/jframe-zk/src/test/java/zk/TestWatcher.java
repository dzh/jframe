/**
* 
*/
package zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

/**
 * @author dzh
 * @date May 4, 2016 6:07:15 PM
 * @since 1.0
 */
public class TestWatcher implements Watcher {

    public TestWatcher() {
    }

    public ZooKeeper startZK(String host) throws Exception {
        ZooKeeper zk = new ZooKeeper(host, 10000, this);
        return zk;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
     */
    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }
    
    @Test
    public void testWatcher() throws Exception {
        String host = "127.0.0.1:2181";
        TestWatcher w = new TestWatcher();
        ZooKeeper zk = w.startZK(host);
        Thread.sleep(60000);
        zk.close();
    }
    
}
