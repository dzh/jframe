package com.alipay.config;

import jframe.ext.util.PropertiesConfig;

import com.alipay.sdk.pay.demo.SignUtils;

/**
 * 
 * @author dzh
 * @date Nov 27, 2014 4:49:43 PM
 * @since 1.0
 */
public class AlipayConfig {

	public static final String PARTNER = "partner";
	public static final String KEY = "key";
	public static final String NOTIFY_URL = "notify_url";
	public static final String SELLER_ID = "seller.id";
	public static final String SUBJECT = "subject";
	public static final String APP_ID = "app.id";
	public static final String PRIVATE_KEY = "private.key";
	public static final String PUBLIC_KEY = "public.key";
	public static final String INPUT_CHARSET = "input_charset";

	public static final String IT_B_PAY = "it_b_pay";

	public static final String PAYMENT_TYPE = "payment_type";

	public static final String ALI_PUBLIC_KEY = "ali_public_key";

	public static final String SIGN_TYPE = "sign.type";
	public static final String SIGN_TYPE_QUERY = "sign.type.query";
	public static final String SERVICE = "service";

	private static PropertiesConfig config = new PropertiesConfig();

	public static void init(String file) throws Exception {
		config.init(file);
	}

	public synchronized static String getConf(String key) {
		return config.getConf("lech", key);
	}

	public static String getSignType() {
		return "sign_type=\"RSA\"";
	}

	public static String sign(String content) {
		return SignUtils.sign(content, getConf(PRIVATE_KEY));
	}

}
