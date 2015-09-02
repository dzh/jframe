/**
 * 
 */
package jframe.pay.dao.service;

import jframe.pay.domain.dao.OrderAlipay;
import jframe.pay.domain.dao.OrderWx;

/**
 * @author dzh
 * @date Sep 2, 2015 2:49:37 AM
 * @since 1.0
 */
public interface OrderDao {

	void insertOrderAlipay(OrderAlipay od);

	OrderAlipay selectOrderAlipay(String payNo);

	OrderAlipay selectOrderAlipayWithOrderNo(String orderNo);

	int updateOrderAlipay(OrderAlipay od);

	void insertOrderWx(OrderWx od);

	OrderWx selectOrderWx(String payNo);

	int updateOrderWx(OrderWx od);

}
