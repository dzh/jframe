/**
 * 
 */
package jframe.pay.domain;

/**
 * @author dzh
 * @date Apr 11, 2016 11:05:30 AM
 * @since 1.0
 */
public enum TradeType {

    APP("APP", "app支付"), NATIVE("NATIVE", "原生扫码支付"), JSAPI("JSAPI", "公众号支付");

    public final String code;
    public final String desc;

    TradeType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
