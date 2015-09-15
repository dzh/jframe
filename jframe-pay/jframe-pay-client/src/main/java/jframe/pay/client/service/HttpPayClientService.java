/**
 * 
 */
package jframe.pay.client.service;

import java.util.HashMap;
import java.util.Map;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.httpclient.service.HttpClientService;
import jframe.pay.domain.Fields;
import jframe.pay.domain.TransType;
import jframe.pay.domain.http.ReqOp;
import jframe.pay.domain.util.HttpUtil;

/**
 * @author dzh
 * @date Aug 31, 2015 5:29:55 PM
 * @since 1.0
 */
@Injector
class HttpPayClientService implements PayClientService, Fields {

    @InjectService(id = "jframe.service.httpclient")
    static HttpClientService HttpClient;

    static String HttpClient_ID = "pay";

    static String Prefix_ord = "/pay/ord/";

    static String Prefix_usr = "/pay/usr/";

    static Map<String, String> HTTP_PARAS = new HashMap<String, String>(1, 1);
    static {
        HTTP_PARAS.put(HttpClientService.P_MIMETYPE,
                "application/x-www-form-urlencoded");
        // HTTP_PARAS.put(HttpClientService.P_METHOD, "post");
    }

    @Override
    public Map<String, Object> pay(Map<String, String> req) throws Exception {
        // check
        // if (HttpUtil.mustReq(req, F_payNo, F_payGroup, F_payType,
        // F_transType)
        // .size() > 0) {
        // RspCode.setRspCode(rsp, RspCode.HTTP_REQ_MUST_EMPTY);
        // return;
        // }
        req.computeIfAbsent(F_reqOp, k -> {
            String transType = req.get(F_transType);
            if (TransType.Consume.code.equals(transType))
                return ReqOp.CONSUME.code;

            return null;
        });
        // TODO remove orderNo

        // TODO check

        return HttpClient.send(HttpClient_ID, Prefix_ord + req.get(F_reqOp),
                HttpUtil.format(req, "utf-8"), null, HTTP_PARAS);
    }

    @Override
    public Map<String, Object> usr(Map<String, String> req) throws Exception {
        // TODO check
        return HttpClient.send(HttpClient_ID, Prefix_usr + req.get(F_reqOp),
                HttpUtil.format(req, "utf-8"), null, HTTP_PARAS);
    }

}
