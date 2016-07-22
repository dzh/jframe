/**
 * 
 */
package jframe.yunsms;

/**
 * 返回状态码
 * 
 * @author dzh
 * @date Jul 9, 2016 4:11:29 PM
 * @since 1.0
 */
public interface YunsmsCode {

    // 发送成功
    String C_100 = "100";
    // 验证失败（帐号密码错误）
    String C_101 = "101";

    // 短信不足
    String C_102 = "102";

}
