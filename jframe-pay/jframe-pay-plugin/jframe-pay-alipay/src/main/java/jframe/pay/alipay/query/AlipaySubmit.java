package jframe.pay.alipay.query;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import jframe.pay.alipay.AlipayConfig;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.sign.MD5;
import com.alipay.util.HttpUtil;

/* *
 *类名：AlipaySubmit
 *功能：支付宝各接口请求提交类
 *详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-13
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipaySubmit {
	static Logger LOG = LoggerFactory.getLogger(AlipaySubmit.class);
	
    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
	
    /**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
	public static String buildRequestMysign(Map<String, String> sPara) {
    	String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if(AlipayConfig.getConf(AlipayConfig.SIGN_TYPE_QUERY).equals("MD5") ) {
        	mysign = MD5.sign(prestr, AlipayConfig.getConf(AlipayConfig.KEY), AlipayConfig.getConf(AlipayConfig.INPUT_CHARSET));
        }
        return mysign;
    }
	
    /**
     * 生成要请求给支付宝的参数数组
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    private static String buildRequestPara(Map<String, String> sParaTemp) {
    	sParaTemp.put("service", "single_trade_query");
    	sParaTemp.put("partner", AlipayConfig.getConf(AlipayConfig.PARTNER));
    	sParaTemp.put("_input_charset", AlipayConfig.getConf(AlipayConfig.INPUT_CHARSET));
        //除去数组中的空值和签名参数
        Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
       //生成签名结果
       String mysign = buildRequestMysign(sPara);
       
       try {
			// 仅需对sign 做URL编码
    	   mysign = URLEncoder.encode(mysign, AlipayConfig.getConf(AlipayConfig.INPUT_CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
       
       //签名结果与签名方式加入请求提交参数组中
       sPara.put("sign", mysign);
       sPara.put("sign_type", AlipayConfig.getConf(AlipayConfig.SIGN_TYPE_QUERY));
       
       //String param = prestr + "&sign=" + mysign + "&sign_type=" + AlipayConfig.getConf(AlipayConfig.SIGN_TYPE);
       String param =  AlipayCore.createLinkString(sPara); 
       return param;
    }

    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果
     * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值
     * 如：buildRequest("", "",sParaTemp)
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath 文件路径
     * @param sParaTemp 请求参数数组
     * @return 支付宝处理结果
     * @throws Exception
     */
    public static Map<String,String> buildRequest(Map<String, String> sParaTemp) throws Exception {
        //待请求参数数组
        String sPara = buildRequestPara(sParaTemp);
        
        String response = HttpUtil.doPost(ALIPAY_GATEWAY_NEW, null, sPara, AlipayConfig.getConf(AlipayConfig.INPUT_CHARSET));
        return parseRespose(response);
    }

    /**
     * 用于防钓鱼，调用接口query_timestamp来获取时间戳的处理函数
     * 注意：远程解析XML出错，与服务器是否支持SSL等配置有关
     * @return 时间戳字符串
     * @throws Exception 
     * @throws UnsupportedEncodingException 
     * @throws IOException
     * @throws DocumentException
     * @throws MalformedURLException
     */
	public static Map<String,String> parseRespose(String reponse) throws Exception {
		Map<String,String> respMap = new HashMap<String,String>();
		
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new ByteArrayInputStream(reponse.getBytes("GBK")));
			Node isSuccessNode = doc.selectSingleNode("//alipay/is_success");
			if (isSuccessNode.getText().equals("T")) {
			    // 判断是否有成功标示
			    Node tradeStatusNode = doc.selectSingleNode("//response/trade/trade_status");
			    respMap.put("trade_status", tradeStatusNode.getText());
			}
			respMap.put("is_success", isSuccessNode.getText());
		} catch (Exception e) {
			LOG.error(e.getMessage(),reponse);
		}
		
		return respMap;
    }
}
