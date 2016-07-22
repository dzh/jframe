/**
 * 
 */
package zk;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
    public void testTimeZone() {
        Date d = new Date();
        System.out.println(d.getTime());

        Calendar c = Calendar.getInstance();
        System.out.println(c.getTimeZone());
        System.out.println(c.getTimeInMillis());
        System.out.println(c.getTimeZone().getRawOffset());
        System.out.println(c.get(Calendar.HOUR_OF_DAY));

        TimeZone t = TimeZone.getTimeZone("Asia/Tokyo");
        c = Calendar.getInstance(t);
        // c.setTimeZone(t);
        System.out.println(c.getTimeZone());
        System.out.println(c.getTimeInMillis());
        System.out.println(c.getTimeZone().getRawOffset());
        System.out.println(c.get(Calendar.HOUR_OF_DAY));

        long l = 3600 * 1000;
        d = new Date(l);
        System.out.println(d.toString());
        
        d= new Date(1466145792000L);
        System.out.println(d.toString());
        //9
        d= new Date(1466142192000L);
        System.out.println(d.toString());
        
        //8
        d= new Date(Long.MAX_VALUE);
        System.out.println(d.toString());
    }

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

    public void testMultiop() {
        // Op.check(path, version)
        // Op.delete(path, version)
        // zk.multi(ops, cb, ctx);
        // zk.multi(ops)
    }

    public void testTransaction() {
        // Transaction t = new Transaction();
        // t.check(path, version);
        // t.create(path, data, acl, createMode)
        // t.delete(path, version);
        // t.commit(cb, ctx);
        // t.commit();
    }

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
