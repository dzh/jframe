package jframe.pay.wx.http.util;

import java.util.Random;

import jframe.pay.wx.http.AccessTokenRequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WxUtil {

	static Logger LOG = LoggerFactory.getLogger(WxUtil.class);
	
	public static String getNonceStr() {
		Random random = new Random();
		return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "GBK");
	}

	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

	/**
	 * 生成微信订单号
	 * 
	 * @return
	 */
	public static String genTradeNo() {
		// ---------------生成订单号 开始------------------------
		// 当前时间 yyyyMMddHHmmss
		String currTime = TenpayUtil.getCurrTime();
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		// 订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
		return strTime + strRandom;
		// ---------------生成订单号 结束------------------------
	}

	public static String getToken() {
		/*if (mem == null) {
			LOG.error("Memcached service is null");
			// TODO
			return null;
		}

		// 获取token值,这样效率更好，TODO 但有一定风险，需要测试
		Object token = mem.get(ServiceConstants.Key_Wx_Access_Token);*/
		Object token = null;
		if (token == null) {
			token = AccessTokenRequestHandler.getTokenReal();
			// less than 7200(default value)
			/*mem.set(ServiceConstants.Key_Wx_Access_Token, token, new Date(
					6600 * 1000));*/
		}
		return token.toString();
	}

}
