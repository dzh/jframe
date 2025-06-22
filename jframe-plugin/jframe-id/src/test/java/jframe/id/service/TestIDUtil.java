package jframe.id.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author dzh
 * @date 2025/6/20 00:28
 */
public class TestIDUtil {

    static Logger LOG = LoggerFactory.getLogger(TestIDUtil.class);

    @Test
    public void testSnowflake() throws Exception {
        SnowflakeIdGenerator id = new SnowflakeIdGenerator("2025-06-01T00:00:00Z", 0, 1, 41, 5, 5, 12);
        LOG.info("id generator: {}", id);
        for (int i = 0; i < 6; i++) {
            LOG.info("{} {}", i, id.nextId());
        }
    }

    @Test
    public void decodeID() {
        long id = 7738971916341248L;
        long epoch = Instant.parse("2025-06-01T00:00:00Z").toEpochMilli();
        long currentTime = id >> 22 + epoch;
        LOG.info("{} {}", currentTime, Instant.ofEpochMilli(currentTime).toString());
    }

    @Test
    public void testZkWorkerPath() {
        SnowflakeService sfs = new SnowflakeService();
        String workerPath = sfs.workerIdZKPath(1);
        LOG.info("workerPath: {}", workerPath);
    }

    @Test
    public void testEpoch() {
        long epoch = System.currentTimeMillis();
        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(epoch));
        LOG.info("{} {} {}", epoch, date, Instant.ofEpochMilli(epoch).toString());
    }

}
