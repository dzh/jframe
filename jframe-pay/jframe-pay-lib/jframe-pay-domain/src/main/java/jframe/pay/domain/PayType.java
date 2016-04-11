/**
 * 
 */
package jframe.pay.domain;

/**
 * @author dzh
 * @date Aug 1, 2014 7:10:47 PM
 * @since 1.0
 */
public enum PayType {
    /**
     * 银联支付
     */
    Y(1, "UPMP"),
    /**
     * 微信支付 APP
     */
    W(2, "WEPAY"),
    /**
     * 支付宝
     */
    A(3, "ALIPAY"),

    ;
    public final int type;
    public final String name;

    private PayType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String toStrVal() {
        return String.valueOf(type);
    }
}
