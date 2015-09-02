/**
 * 
 */
package jframe.pay.dao.service;

import jframe.pay.domain.dao.UsrAccount;

/**
 * @author dzh
 * @date Sep 2, 2015 2:49:26 AM
 * @since 1.0
 */
public interface UsrDao {

	void insertUsrAccount(UsrAccount usr);

	UsrAccount selectUsrAccount(String account);

	int updateUsrAccount(UsrAccount usr);

}
