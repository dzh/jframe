/**
 *
 */
package jframe.pay.client.service;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.httpclient.service.HttpClientService;
import jframe.pay.client.PayClientConf;
import jframe.pay.client.PayClientPlugin;
import jframe.pay.domain.Fields;
import jframe.pay.domain.TransType;
import jframe.pay.domain.http.ReqOp;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.domain.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dzh
 * @date Aug 31, 2015 5:29:55 PM
 * @since 1.0
 */
@Injector
class HttpPayClientService implements PayClientService, Fields {

    static final Logger LOG = LoggerFactory.getLogger(HttpPayClientService.class);

    @InjectPlugin
    static PayClientPlugin Plugin;

    @InjectService(id = "jframe.service.httpclient")
    static HttpClientService HttpClient;

    static final String HttpClient_ID = "pay";

    static final String Prefix_ord = "/pay/ord/";

    static final String Prefix_usr = "/pay/usr/";

    static final Map<String, String> HTTP_PARAS = new HashMap<String, String>(1, 1);

    static final PayClientConf _conf = new PayClientConf();

    static {
        HTTP_PARAS.put(HttpClientService.P_MIMETYPE, "application/x-www-form-urlencoded");
        // HTTP_PARAS.put(HttpClientService.P_METHOD, "post");
    }

    static final String FILE_CONF = "file.payclient";

    @Start
    void start() {
        LOG.info("HttpPayClientService is starting!");

        String path = Plugin.getConfig(FILE_CONF, "");
        try {
            if (new File(path).exists()) {
                _conf.init(path);
                LOG.info("HttpPayClientService load {} successfully!", path);
            } else {
                LOG.info("HttpPayClientService not found file -> {}", path);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        LOG.info("HttpPayClientService start successfully!");
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

        req.putIfAbsent(PayClientConf.K_Pay_Version, _conf.getConf(null, PayClientConf.K_Pay_Version));
        req.putIfAbsent(F_backUrl, _conf.getConf(null, PayClientConf.Pre_Pay_Req + F_backUrl));

        req.computeIfAbsent(F_reqOp, k -> {
            String transType = req.get(F_transType);
            if (TransType.Consume.code.equals(transType))
                return ReqOp.CONSUME.code;

            return null;
        });
        // TODO remove orderNo

        // TODO check

        String result = HttpClient.send(HttpClient_ID, Prefix_ord + req.get(F_reqOp), HttpUtil.format(req, "utf-8"), null,
                HTTP_PARAS);
        return JsonUtil.decode(result);
    }

    @Override
    public Map<String, Object> usr(Map<String, String> req) throws Exception {
        // TODO check
        String result = HttpClient.send(HttpClient_ID, Prefix_usr + req.get(F_reqOp), HttpUtil.format(req, "utf-8"), null,
                HTTP_PARAS);
        return JsonUtil.decode(result);
    }

}
