package jframe.zk;

/**
 * @author dzh
 * @date Dec 12, 2018 7:18:31 PM
 * @version 0.0.1
 */
public interface ZkField {

    String Namespace = "namespace";
    String ConnectString = "connectString";

    String ConnectTimeout = "connectTimeout"; // ms
    String SessionTimeout = "sessionTimtout"; // ms

    String RetryInterval = "retryInterval"; // ms
    String RetryTimes = "retryTimes";

}
