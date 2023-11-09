/**
 *
 */
package jframe.pay.domain.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * @author dzh
 * @date Jul 31, 2014 9:04:52 PM
 * @since 1.0
 */
public class IDUtil {

    /**
     * 系统广播组ID
     */
    public static final String Sys_MsgGroupID = "0";

    /**
     * 系统用户ID
     */
    public static final Long Sys_UsrId = 0L;

    /**
     * 生成短信验证码 4位数字
     *
     * @return
     */
    public static String genVCode() {
        StringBuilder buf = new StringBuilder();

        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    /*
     * @Deprecated public static int genAccessToken() { int val = 0; int random
     * = 0; for (int i = 0; i < 4; i++) { random = (int) (Math.random() * 10);
     * val += (random == 0 ? 1 : random) * (int) Math.pow(10, i); } return val;
     * }
     */
    public static String genAccessToken(long usrId) {
        StringBuilder buf = new StringBuilder();

        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append(usrId);
        return buf.toString();
    }

    public static long genUsrIdFromToken(String token) throws NumberFormatException {
        return Long.parseLong(token.substring(4));
    }

    public static String genOrderNo() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        // buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genUsrAccount() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        // buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genPayNo() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        // buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genPayGroupNo() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        // buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genFlowNo() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genOrderDriverNo() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        // buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genTicketNo() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        // buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genSysEventId() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        // buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genSysTaskId() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        // buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    /**
     * 线路ID
     *
     * @return
     */
    public static String genPathId() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genSubjectId() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genMsgId() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    public static String genMsgGroupId() {
        StringBuilder buf = new StringBuilder();

        Calendar calc = Calendar.getInstance();
        buf.append(calc.get(Calendar.ZONE_OFFSET) / 3600000);

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        buf.append(fmt.format(calc.getTime()));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));
        buf.append((int) (Math.random() * 10));

        return buf.toString();
    }

    /**
     * 消息头的key
     *
     * @return
     */
    public static String genSignKey() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            int category = new Random().nextInt() % 3;
            char ch = '0';
            switch (category) {
                case 0:
                    ch = (char) ((new Random().nextInt(10) % 10 + 48) & 0xffff);
                    break;
                case 1:
                    ch = (char) ((new Random().nextInt(26) % 26 + 65) & 0xffff);
                    break;
                case 2:
                    ch = (char) ((new Random().nextInt(26) % 26 + 97) & 0xffff);
                    break;
            }
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
     * 生成邀请码
     *
     * @return
     */
    public static String genInviteCode() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            buf.append((char) ((new Random().nextInt(26) % 26 + 97) & 0xffff));
        }
        return buf.toString();
    }

}
