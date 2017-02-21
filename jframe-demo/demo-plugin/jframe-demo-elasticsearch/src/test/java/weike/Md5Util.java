package weike;

import java.security.MessageDigest;

/**
 * md5工具类
 */
public class Md5Util {

    private static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };

    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串摘要
     */
    public static String md5Str(String data) {
        return bytesToHex(getMessageDigest().digest(data.getBytes()));
    }

    private static String bytesToHex(byte bytes[]) {
        return new String(encodeHex(bytes, hexDigits));
    }

    /*
     * copy from common-codec at @org.apache.commons.codec.digest.DigestUtils
     */
    private static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

}
