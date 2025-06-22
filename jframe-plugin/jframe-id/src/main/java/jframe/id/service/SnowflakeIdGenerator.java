package jframe.id.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * +------------------+----------------+----------------+----------------+
 * |  1位符号位       |  41位时间戳    |  5位数据中心ID  |  5位机器ID     |  12位序列号    |
 * +------------------+----------------+----------------+----------------+
 * 雪花算法 ID 结构
 * 一个标准的雪花算法 ID 由以下部分组成（总长度 64 位）：
 * 部分	位数	说明
 * 符号位	1 位	固定为 0，表示正数
 * 时间戳	41 位	记录生成 ID 的时间戳（毫秒级），支持约 69 年的时间范围
 * 工作机器 ID	10 位	用于标识不同的机器节点，最多支持 1024 个节点（5 位数据中心 ID + 5 位机器 ID）
 * 序列号	12 位	同一毫秒内生成的不同 ID，每毫秒最多生成 4096 个 ID
 * <p>
 * <p>
 * 组合	数据中心数	每中心机器数	全局总机器数	适用场景
 * 5+5	32	32	1,024	中小型分布式系统
 * 4+6	16	64	1,024	数据中心少但规模大
 * 3+7	8	128	1,024	容器化环境（Pod 动态创建）
 * 2+10	4	1,024	4,096	超大规模单数据中心
 *
 * @author dzh
 * @date 2025/6/19 23:12
 */
public class SnowflakeIdGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(SnowflakeIdGenerator.class);
    // 开始时间戳 e.g. Instant.parse("2025-06-01T00:00:00Z").toEpochMilli();
    private final long epoch;
    // 位移定义
//    private final int timestampBits;  // 扩展为42位，支持138年
//    private final int dataCenterIdBits;
//    private final int workerIdBits;
//    private final int sequenceBits;

    //左移位数
//    private final int workerIdShift;
//    private final int dataCenterIdShift;
    private final int timestampShift;

    // 计算时间戳上限（毫秒）
    private final long maxTimestamp;
    //~(-1L << dataCenterIdBits)
//    private long maxDataCenterId = (1L << dataCenterIdBits) - 1;//8
    //~(-1L << workerIdBits)
//    private long maxWorkerId = (1L << workerIdBits) - 1;//64
    // ~(-1L << sequenceBits);
    private final long maxSequence;// 4095

    // 左移后的值
    private final long dataCenterIdOffset;
    private final long workerIdOffset;

    // 使用原子变量替代普通变量
    private final AtomicLong lastTimestamp = new AtomicLong(-1);
    private final AtomicLong sequence = new AtomicLong(0);

    // 单例模式：使用静态内部类实现延迟加载和线程安全
//    private static class SingletonHolder {
//        private static final SnowflakeIdGenerator INSTANCE;
//
//        static {
//            try {
//                // 从机器IP自动生成dataCenterId和workerId
//                INSTANCE = new SnowflakeIdGenerator();
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to initialize SnowflakeIdGenerator", e);
//            }
//        }
//    }

    // 获取单例实例的静态方法
//    public static SnowflakeIdGenerator getInstance() {
//        return SingletonHolder.INSTANCE;
//    }

    // 私有构造函数，防止外部实例化
    public SnowflakeIdGenerator(String epoch, long dataCenterId, long workerId, int timestampBits, int dataCenterIdBits, int workerIdBits, int sequenceBits) throws Exception {
        if (epoch == null || epoch.isEmpty()) throw new IllegalArgumentException("epoch is empty");
        this.epoch = Instant.parse(epoch).toEpochMilli();

        int sunBits = timestampBits + dataCenterIdBits + workerIdBits + sequenceBits;
        if (sunBits != 63) {
            throw new IllegalArgumentException("timestampBits+dataCenterId+workerIdBits+sequenceBits != 63,sumBits=" + sunBits);
        }
        //设置左移数量
        int workerIdShift = sequenceBits;
        int dataCenterIdShift = sequenceBits + workerIdBits;
        this.timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;
        // 计算时间戳上限（毫秒）
        this.maxTimestamp = (1L << timestampBits) - 1;
        // 计算最大序列号
        this.maxSequence = (1L << sequenceBits) - 1;// 4095

        long maxDataCenterId = (1L << dataCenterIdBits) - 1;//8
        // 校验生成的ID
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException("Invalid dataCenterId: " + dataCenterId);
        }
        long maxWorkerId = (1L << workerIdBits) - 1;//64
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Invalid workerId: " + workerId);
        }
        this.dataCenterIdOffset = dataCenterId << dataCenterIdShift;
        this.workerIdOffset = workerId << workerIdShift;

        LOG.info("new SnowflakeIdGenerator(epoch={}, dataCenterId={}, workerId={}, timestampBits={}, dataCenterIdBits={}, workerIdBits={}, sequenceBits={})", epoch, dataCenterId, workerId, timestampBits, dataCenterIdBits, workerIdBits, sequenceBits);
//        LOG.info("SnowflakeIdGenerator initialized with DataCenterID: {}, workerId: {}", dataCenterId, workerId);
    }

    // 无锁化ID生成方法（保持不变）
    public long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // 检查时间戳是否超出限制
        long elapsedTime = currentTimestamp - epoch;
        if (elapsedTime > maxTimestamp) {
            throw new RuntimeException("Timestamp limit exceeded: " + elapsedTime + "ms");
        }

        long oldTimestamp;
        long oldSequence;
        long newSequence;
        // 处理时钟回拨（先检查再获取锁）
        if (currentTimestamp < lastTimestamp.get()) {
            throw new RuntimeException("Clock moved backwards...");
        }

        // 无锁循环CAS更新序列号
        do {
            oldTimestamp = lastTimestamp.get();

            if (currentTimestamp > oldTimestamp) {
                // 新的毫秒，重置序列号
                if (sequence.compareAndSet(sequence.get(), 0)) {
                    break;
                }
            } else if (currentTimestamp == oldTimestamp) {
                // 同一毫秒，尝试递增序列号
                oldSequence = sequence.get();
                newSequence = (oldSequence + 1) & maxSequence;
                if (newSequence == 0) {
                    // 序列号用尽，等待下一毫秒
                    currentTimestamp = waitNextMillis(oldTimestamp);
                    continue;
                }
                if (sequence.compareAndSet(oldSequence, newSequence)) {
                    break;
                }
            } else {
                // currentTimestamp < oldTimestamp 已在前面处理
                throw new IllegalStateException("Invalid timestamp state");
            }
        } while (true);

        // 更新lastTimestamp（使用CAS保证原子性）
        if (!lastTimestamp.compareAndSet(oldTimestamp, currentTimestamp)) {
            throw new IllegalStateException("Failed to update timestamp");
        }

        // 生成ID
        return ((currentTimestamp - epoch) << timestampShift) | this.dataCenterIdOffset | this.workerIdOffset | sequence.get();
    }

    // 等待下一毫秒（保持不变）
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    @Override
    public String toString() {
//        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(epoch));
        String date = Instant.ofEpochMilli(epoch).toString();
        return String.format("epoch=%s, dataCenterIdOffset=%s, workerIdOffset=%s, timestampShift=%s, maxTimestamp=%s, maxSequence=%s", date, dataCenterIdOffset, workerIdOffset, timestampShift, maxTimestamp, maxSequence);
    }

    // 测试示例
//    public static void main(String[] args) {
//        // 获取单例实例并生成ID
//        try {
//            SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
//            // 生成10个ID测试
//            for (int i = 0; i < 10; i++) {
//                System.out.println(generator.nextId());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }
}
