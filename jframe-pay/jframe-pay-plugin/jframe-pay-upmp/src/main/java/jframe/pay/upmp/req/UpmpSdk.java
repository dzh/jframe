/**
 * 
 */
package jframe.pay.upmp.req;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unionpay.acp.sdk.HttpClient;
import com.unionpay.acp.sdk.SDKUtil;

import jframe.pay.upmp.domain.UpmpFields;

/**
 * @author dzh
 * @date Nov 25, 2015 4:15:56 PM
 * @since 1.0
 */
public class UpmpSdk implements UpmpFields {

    protected static Logger LOG = LoggerFactory.getLogger(UpmpSdk.class);

    public static String encoding = "UTF-8";

    /**
     * java main方法 数据提交 对数据进行签名
     * 
     * @param contentData
     * @return 签名后的map对象
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> signData(Map<String, ?> contentData) {
        Entry<String, String> obj = null;
        Map<String, String> submitFromData = new HashMap<String, String>();
        for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
            obj = (Entry<String, String>) it.next();
            String value = obj.getValue();
            if (StringUtils.isNotBlank(value)) {
                // 对value值进行去除前后空处理
                submitFromData.put(obj.getKey(), value.trim());
               // System.out.println(obj.getKey() + "-->" + String.valueOf(value));
            }
        }
        /**
         * 签名
         */
        SDKUtil.sign(submitFromData, encoding);

        return submitFromData;
    }

    /**
     * java main方法 数据提交 提交到后台
     * 
     * @param contentData
     * @return 返回报文 map
     */
    public static Map<String, String> submitUrl(Map<String, String> submitFromData, String requestUrl) {
        String resultString = "";
        // System.out.println("requestUrl====" + requestUrl);
        // System.out.println("submitFromData====" + submitFromData.toString());
        /**
         * 发送
         */
        HttpClient hc = new HttpClient(requestUrl, 30000, 30000);
        try {
            int status = hc.send(submitFromData, encoding);
            if (200 == status) {
                resultString = hc.getResult();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        Map<String, String> resData = new HashMap<String, String>();
        /**
         * 验证签名
         */
        if (null != resultString && !"".equals(resultString)) {
            // 将返回结果转换为map
            resData = SDKUtil.convertResultStringToMap(resultString);
            if (SDKUtil.validate(resData, encoding)) {
                LOG.info("验证签名成功");
            } else {
                LOG.info("验证签名失败");
            }
            // 打印返回报文
            // System.out.println("打印返回报文：" + resultString);
        }
        return resData;
    }

}
