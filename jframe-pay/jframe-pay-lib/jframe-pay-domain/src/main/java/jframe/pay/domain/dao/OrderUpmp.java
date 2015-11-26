/**
 * 
 */
package jframe.pay.domain.dao;

/**
 * @author dzh
 * @date Sep 16, 2015 2:13:09 PM
 * @since 1.0
 */
public class OrderUpmp extends OrderBase {
    /**
     * TODO 支付支持的卡类型，'{cardType=01}'-借记卡，'{cardType=02}'-信用卡，不填都支持
     */
    public String merReserved;

    public String version;

    public String traceNo;

}
