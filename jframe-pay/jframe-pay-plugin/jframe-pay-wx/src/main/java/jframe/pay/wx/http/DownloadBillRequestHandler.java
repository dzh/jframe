package jframe.pay.wx.http;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import jframe.pay.wx.domain.WxFields;
import jframe.pay.wx.http.client.TenpayHttpClient;
import jframe.pay.wx.http.client.WxServiceNew;
import jframe.pay.wx.http.util.MD5Util;

public class DownloadBillRequestHandler extends RequestHandler {

    public DownloadBillRequestHandler() {
        super();

    }

    /**
     * 创建md5摘要,规则是:按参数固定顺序组串,遇到空值的参数不参加签名。
     */
    protected void createSign() {
        StringBuffer sb = new StringBuffer();
        sb.append("spid=" + this.getParameter("spid") + "&");
        sb.append("trans_time=" + this.getParameter("trans_time") + "&");
        sb.append("stamp=" + this.getParameter("stamp") + "&");
        sb.append("cft_signtype=" + this.getParameter("cft_signtype") + "&");
        sb.append("mchtype=" + this.getParameter("mchtype") + "&");
        sb.append("key=" + this.getKey());

        String enc = "";
        String sign = MD5Util.MD5Encode(sb.toString(), enc).toLowerCase();

        this.setParameter("sign", sign);

        // debug信息
        this.setDebugInfo(sb.toString() + " => sign:" + sign);

    }

    private static String genNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "");
        // return
        // MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public static void main(String[] args) throws Exception {
        TenpayHttpClient client = new TenpayHttpClient();

        String date = "20160406";
        String appid = "wx*********5c";
        String mchid = "12*****2";
        String appkey = "1234*********op12";
        // ALL SUCCESS REFUND
        String billType = "ALL";

        List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
        packageParams.add(new BasicNameValuePair(WxFields.F_appid, appid));
        packageParams.add(new BasicNameValuePair(WxFields.F_bill_date, date));
        packageParams.add(new BasicNameValuePair(WxFields.F_bill_type, billType));
        packageParams.add(new BasicNameValuePair(WxFields.F_mch_id, mchid));
        packageParams.add(new BasicNameValuePair(WxFields.F_nonce_str, genNonceStr()));
        String sign = genPackageSign(packageParams, appkey);
        packageParams.add(new BasicNameValuePair("sign", sign));
        String entity = WxServiceNew.toXml(packageParams);

        // byte[] buf = httpPost(url, entity);
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setReqContent(entity);

        client.callHttpPost("https://api.mch.weixin.qq.com/pay/downloadbill", entity);
        String rspContent = client.getResContent();
        System.out.println(rspContent);
        String file = "/Users/dzh/share/ody/opay/bl/bl-bill-" + date + ".txt";
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(rspContent.getBytes("utf-8"));
        fos.flush();
        fos.close();

        BufferedReader in = new BufferedReader(new StringReader(rspContent));
        String line = null;
        while ((line = in.readLine()) != null) {
            String[] arr = line.split(",");
            if (arr.length > 21)
                System.out.println(arr[0] + "->" + arr[6] + "->" + arr[9] + "->" + arr[21]);
        }
    }

    public static String genPackageSign(List<NameValuePair> params, String appkey) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(appkey);

        String packageSign = WxServiceNew.getMessageDigest(sb.toString().getBytes("UTF-8")).toUpperCase();

        // String packageSign = MD5Util.MD5Encode(sb.toString(),
        // "").toUpperCase();
        return packageSign;
    }
}
