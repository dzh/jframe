package jframe.id;

/**
 * @author dzh
 * @date 2025/6/21 16:42
 */
public interface IdField {
    /******snowflake******/
    String SNOWFLAKE_EPOCH = "snowflake.epoch";
    String SNOWFLAKE_WORKER_ID = "snowflake.worker.id";
    String SNOWFLAKE_DATACENTER_ID = "snowflake.datacenter.id";
    // the number of binary digits
    String SNOWFLAKE_TIMESTAMP_BITS = "snowflake.timestamp.bits";
    String SNOWFLAKE_DATACENTER_BITS = "snowflake.datacenter.bits";
    String SNOWFLAKE_WORKER_BITS = "snowflake.worker.bits";
    String SNOWFLAKE_SEQUENCE_BITS = "snowflake.sequence.bits";
    // worker id generation method
    String SNOWFLAKE_WORKER_GENERATION = "SNOWFLAKE.WORKER.generation";
    String SNOWFLAKE_WORKER_ENV_IP = "snowflake.worker.env.ip";//worker's ip environment variable name
    // zk
    String SNOWFLAKE_ZK_ID = "snowflake.zk.id"; // jframe-zk curator group id
    String SNOWFLAKE_ZK_WORKER_PATH = "snowflake.zk.worker.path";
    //
    String GENERATION_IP = "ip";
    String GENERATION_ZK = "zk";
    //default env name
    String ENV_HOST_IP = "HOST_IP";
    //default zk id
    String ZK_ID = "snowflake";
    String ZK_WORKER_PATH = "/snowflake/worker_ids";
}
