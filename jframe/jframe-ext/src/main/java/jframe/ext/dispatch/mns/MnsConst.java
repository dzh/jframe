package jframe.ext.dispatch.mns;

/**
 * @author dzh
 * @date 2022/11/18 11:06
 */
public interface MnsConst {

    String FILE_MNS = "file.mns";

    // conf field
    String MNS_ACCESSKEYID = "mns.accesskeyid";
    String MNS_ACCESSKEYSECRET = "mns.accesskeysecret";
    String MNS_ACCOUNTENDPOINT = "mns.accountendpoint";
    String MNS_QUEUE = "mns.queue"; //queue name, e.g. queueName1 queueName2

    // msg meta
    String M_MNS_CODEC = "m.mns.codec";
    String M_MNS_QUEUE = "m.mns.queue";

    // default topic
    String DEFAULT_QUEUE = "jframe";
}
