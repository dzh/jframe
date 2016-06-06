/**
 * 
 */
package zk;

import java.io.IOException;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date May 24, 2016 11:08:13 PM
 * @since 1.0
 */
public class TestApi {

    static Logger LOG = LoggerFactory.getLogger(TestApi.class);

    private ZooKeeper zk;

    @Before
    public void initZk() throws IOException {
        zk = new ZooKeeper("127.0.0.1:2181", 10000, null);
    }

    @After
    public void closeZk() throws IOException, InterruptedException {
        if (zk != null) {
            zk.close();
        }
    }

    @Test
    public void testCreate() {
        class CreateCallback implements StringCallback {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                LOG.info("code->{} path->{}", rc, path);
                switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    // TODO re-create
                    break;
                case OK:
                    break;
                case NODEEXISTS:
                    break;
                default:
                    LOG.error("error code->{} path->{}", rc, path);
                }
            }

        }

        if (zk != null)
            zk.create("/test", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new CreateCallback(), null);
    }

    public void testDelete() {
        // zk.delete(path, version, cb, ctx);
    }

    public void testExists() {

    }

    public void testSetData() {

    }

    public void testGetChildren() {

    }

    @Test
    public void testStat() throws KeeperException, InterruptedException {
        String path = "/";
        listStat(path);
    }

    private void listStat(String path) throws KeeperException, InterruptedException {
        for (String child : zk.getChildren(path, false)) {
            Stat stat = new Stat();
            byte[] data = zk.getData(path, false, stat);
            LOG.info(stat.toString());

            listStat(path + "/" + child);
        }
    }

}
