/**
 * 
 */
package jframe.pay.alipay.domain;

/**
 * @author dzh
 * @date Nov 26, 2014 4:26:15 PM
 * @since 1.0
 */
public interface TradeStatus {

	String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
	String TRADE_CLOSED = "TRADE_CLOSED";
	String TRADE_SUCCESS = "TRADE_SUCCESS";
	String TRADE_FINISHED = "TRADE_FINISHED";

	String REFUND_SUCCESS = "REFUND_SUCCESS";
	String REFUND_CLOSED = "REFUND_CLOSED";
}
