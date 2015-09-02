/**
 * 
 */
package jframe.pay.domain;

/**
 * 
 * @author dzh
 * @date Jul 24, 2014 9:43:16 AM
 * @since 1.0
 */
public enum PayCurrency {

	CNY("156", "人民币"), USD("840", "美元"), EUR("978", ""), JPY("392", ""), CHF(
			"756", ""), AUD("036", ""), CAD("124", ""), HKD("344", ""), GBP(
			"826", ""), MOP("446", ""), SGD("702", ""), TWD("901", ""), IDR(
			"360", "");

	public final String code;
	public final String desc;

	PayCurrency(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

}

// 代码 含义
// 156 人民币
// 840 美元
// 978 欧元
// 392 日元
// 756 瑞士法郎
// 036 澳大利亚元
// 124 加拿大元
// 344 港币
// 826 英镑
// 446 澳门元
// 702 新加坡元
// 901 新台币
// 360 印尼盾
