package jframe.demo.elasticsearch.weike;

/**
 * 会员基础字段
 *
 * @author sume
 */
public class BaseMemberDO extends AbstractDO {

    private static final long serialVersionUID = -7188505947243040835L;

    public static final String DEBUG_BUYER_NICK = "菜菇凉的小蘑菇";
    public static final String DEBUG_RECEIVER_NAME = "诸葛神侯";

    /**
     * 会员昵称
     */
    private String buyerNick;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮件地址
     */
    private String email;
    /**
     * 收件人地址
     */
    private String receiverName;
    /**
     * 是否有给过中差评
     */
    private boolean giveNBRate;
    /**
     * 有过退款
     */
    private boolean hasRefund;
    /**
     * 最近发送短信时间
     */
    private long lastSmsTime;
    /**
     * 最近发送邮件时间
     */
    private long lastEdmTime;
    /**
     * 是否被拉黑
     */
    private boolean black;

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public boolean isGiveNBRate() {
        return giveNBRate;
    }

    public void setGiveNBRate(boolean giveNBRate) {
        this.giveNBRate = giveNBRate;
    }

    public long getLastSmsTime() {
        return lastSmsTime;
    }

    public void setLastSmsTime(long lastSmsTime) {
        this.lastSmsTime = lastSmsTime;
    }

    public boolean isBlack() {
        return black;
    }

    public void setBlack(boolean black) {
        this.black = black;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastEdmTime() {
        return lastEdmTime;
    }

    public void setLastEdmTime(long lastEdmTime) {
        this.lastEdmTime = lastEdmTime;
    }

    public boolean isHasRefund() {
        return hasRefund;
    }

    public void setHasRefund(boolean hasRefund) {
        this.hasRefund = hasRefund;
    }

    /**
     * 生成测试会员
     *
     * @param mobile
     * @param email
     * @return
     */
    public static BaseMemberDO createTest(String mobile, String email) {
        BaseMemberDO baseMemberDO = new BaseMemberDO();
        baseMemberDO.setBuyerNick(DEBUG_BUYER_NICK);// 旺旺
        baseMemberDO.setReceiverName(DEBUG_RECEIVER_NAME);// 收货人
        baseMemberDO.setMobile(mobile);
        baseMemberDO.setEmail(email);
        return baseMemberDO;
    }
}
