/**
 * 
 */
package jframe.core.msg;

/**
 * jframe's configuration modification message.
 * 
 * @author dzh
 * @date Nov 19, 2013 11:19:09 AM
 * @since 1.0
 */
public class ConfigMsg extends TextMsg {

    public static final ConfigMsg createMsg(String key, String oldVal, String newVal) {
        ConfigMsg msg = new ConfigMsg();
        msg.setMeta("key", key);
        msg.setMeta("old", oldVal);
        msg.setMeta("new", newVal);
        return msg;
    }

    // public static final ConfigMsg createMsg(String file) {
    // ConfigMsg msg = new ConfigMsg();
    // msg.setMeta("file", file);
    // return msg;
    // }

    public String getOldVal() {
        return (String) getMeta("old");
    }

    public String getNewVal() {
        return (String) getMeta("new");
    }

    public String getKey() {
        return (String) getMeta("key");
    }

    // public String getFile() {
    // return getMeta("file");
    // }

    // public boolean isConfig() {
    // return getMeta("key") != null;
    // }

    // public boolean isFile() {
    // return getMeta("file") != null;
    // }

    public String toString() {
        // if (getFile() != null)
        // return "file: " + getFile();
        // else
        return "key: " + getKey() + ", oldVal: " + getOldVal() + ", newVal: " + getNewVal();
    }
}
