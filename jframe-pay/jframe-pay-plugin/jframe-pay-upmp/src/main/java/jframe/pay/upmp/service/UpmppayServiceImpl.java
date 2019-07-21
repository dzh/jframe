/**
 *
 */
package jframe.pay.upmp.service;

import com.unionpay.acp.sdk.SDKConfig;
import jframe.core.conf.VarHandler;
import jframe.core.plugin.annotation.*;
import jframe.core.util.PropsConf;
import jframe.httpclient.service.HttpClientService;
import jframe.memcached.client.MemcachedService;
import jframe.pay.dao.service.PayDaoService;
import jframe.pay.domain.Fields;
import jframe.pay.domain.PayCurrency;
import jframe.pay.domain.PayStatus;
import jframe.pay.domain.dao.OrderUpmp;
import jframe.pay.domain.http.RspCode;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.domain.util.JsonUtil;
import jframe.pay.domain.util.ObjectUtil;
import jframe.pay.upmp.UpmppayPlugin;
import jframe.pay.upmp.domain.UpmpConfig;
import jframe.pay.upmp.domain.UpmpFields;
import jframe.pay.upmp.req.ConsumeReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dzh
 * @date Nov 24, 2015 5:24:41 PM
 * @since 1.0
 */
@Injector
public class UpmppayServiceImpl implements UpmppayService, UpmpFields {

    static Logger LOG = LoggerFactory.getLogger(UpmppayServiceImpl.class);

    @InjectService(id = "jframe.pay.service.dao")
    static PayDaoService PayDao;

    @InjectPlugin
    static UpmppayPlugin Plugin;

    @InjectService(id = "jframe.service.httpclient")
    static HttpClientService HttpClient;
    static Map<String, String> HTTP_PARAS = new HashMap<String, String>(1, 1);

    static {
        HTTP_PARAS.put(HttpClientService.P_MIMETYPE, "application/x-www-form-urlencoded");
        HTTP_PARAS.put(HttpClientService.P_METHOD, "post");
    }

    @InjectService(id = "jframe.service.memcached.client")
    static MemcachedService MemSvc;

    @Start
    void start() {
        try {
            UpmpConfig.GroupID = Plugin.getConfig("groupid.upmppay", UpmpConfig.GroupID);
            UpmpConfig.init(Plugin.getConfig(UpmpConfig.CONF_FILE_NAME));
            VarHandler vh = new VarHandler(Plugin.getContext().getConfig());
            UpmpConfig.config.replace(vh);

            PropsConf acpSDK = new PropsConf();
            acpSDK.init(UpmpConfig.getConf(UpmpConfig.KEY_ACP_SDK));
            acpSDK.replace(vh);
            SDKConfig.getConfig().loadProperties(acpSDK.clone2Properties());
        } catch (Exception e) {
            LOG.error("Load wxconfig error {}", e.getMessage());
        }
        LOG.info("UpmppayServiceImpl start successfully!");
    }

    @Stop
    void stop() {

    }

    @Override
    public void pay(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        // check req
        if (HttpUtil.mustReq(req, F_payNo, F_transType, F_payAmount, F_payDesc).size() > 0) {
            RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
            return;
        }

        if (PayDao == null) {
            RspCode.setRspCode(rsp, RspCode.FAIL_DB_Conn);
            return;
        }

        rsp.put(F_filter, JsonUtil.createPropertyFilter(new String[]{F_rspCode, F_rspDesc, F_tn}));

        if (ConsumeReq.consume(req, rsp)) {
            OrderUpmp od = PayDao.selectOrderUpmp(req.get(F_payNo));
            boolean insert = false;
            if (od == null) {
                insert = true;

                od = new OrderUpmp();
                od.payNo = req.get(F_payNo);
                od.payStatus = PayStatus.C_PAY_WAIT.code;
                od.version = UpmpConfig.getConf(UpmpConfig.KEY_VERSION);
            } else {
                // TODO 已成功单子处理
            }

            od.payDesc = req.get(F_payDesc);
            od.transType = req.get(F_transType);
            od.payAmount = Long.parseLong(req.get(F_payAmount));
            od.payGroup = req.get(F_payGroup);
            od.payCurrency = req.getOrDefault(F_payCurrency, PayCurrency.CNY.code);
            od.payTimeout = Long.parseLong(req.getOrDefault(F_payTimeout, "-1"));
            if (od.payTimeout == -1) {
                // TODO config
                od.payTimeout = new Date().getTime() + 72 * 3600 * 1000;
            }
            od.backUrl = req.get(F_backUrl);
            od.account = req.get(F_account);
            od.orderNo = req.get(F_orderNo);

            if (insert) {
                PayDao.insertOrderUpmp(od);
            } else
                PayDao.updateOrderUpmp(od);

            // TODO insert order detail
            // TODO insert task

            RspCode.setRspCode(rsp, RspCode.SUCCESS);
            return;
        }

        // rsp.clear();
        if (!rsp.containsKey(Fields.F_rspCode))
            RspCode.setRspCode(rsp, RspCode.FAIL_NET);
    }

    static final ConcurrentMap<String, String> PayBackLock = new ConcurrentHashMap<>();

    private boolean chkPayBack(Map<String, String> req) {
        String od = req.get(UpmpFields.F_orderId);
        String ood = PayBackLock.putIfAbsent(od, "");
        boolean chk = ood == null ? true : false;
        if (chk) {
            chk = MemSvc.get("upmp.payback." + od) == null ? true : false;
        }

        // clean
        if (ood == null && !chk)
            PayBackLock.remove(od);

        return chk;
    }

    @Override
    public void payBack(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        if (!chkPayBack(req)) {
            return;
        }

        try {
            // for cluster
            MemSvc.add("upmp.payback." + req.get(UpmpFields.F_orderId), "1", new Date(15 * 1000));
            String orderStatus = PayStatus.C_PAY_FAIL.code;
            if (ConsumeReq.backPay(req, rsp)) {
                orderStatus = PayStatus.C_PAY_SUC.code;
            }
            req.put(F_payStatus, orderStatus);
            upmpPayBack(req, rsp);
        } finally {
            PayBackLock.remove(req.get(UpmpFields.F_orderId));
            MemSvc.delete("upmp.payback." + req.get(UpmpFields.F_orderId));
        }
    }

    private void upmpPayBack(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        String orderNo = req.get(UpmpFields.F_orderId);
        OrderUpmp od = PayDao.selectOrderUpmpWithOrderNo(orderNo);
        if (od == null)
            throw new Exception("OrderUpmp is not found, orderNo ->" + orderNo);

        if (PayStatus.C_PAY_SUC.code.equals(od.payStatus)) {
            RspCode.setRspCode(rsp, RspCode.SUCCESS);
            return;
        }

        // od.tradeMode = Integer.parseInt(req.get(WxFields.F_trade_mode));
        od.payStatus = req.get(F_payStatus);
        od.traceNo = req.get(F_traceNo);
        od.orderFinishTime = new Date().getTime();

        PayDao.updateOrderUpmp(od);

        // TODO postBack until return success
        String orderStatus = od.payStatus;
        if (ObjectUtil.notEmpty(od.backUrl) && (PayStatus.C_PAY_SUC.code.equals(orderStatus)
                || PayStatus.C_PAY_FAIL.code.equals(orderStatus) || PayStatus.C_PAY_TIMEOUT.code.equals(orderStatus))) {
            postBack(od);
        } else {
            LOG.error("upmp callback orderStatus->{}", orderStatus);
        }

    }

    public void postBack(OrderUpmp od) {
        Map<String, String> map = new HashMap<String, String>(2, 1);
        map.put(F_payNo, od.payNo);
        map.put(F_payStatus, od.payStatus);

        boolean succ = false;
        try {
            URL url = new URL(od.backUrl);
            int port = url.getPort() == -1 ? 80 : url.getPort();

            LOG.info("postBack -> {},{},{}", url, new Date(), map);
            Long backtime = System.currentTimeMillis();

            Map<String, String> paras = new HashMap<>(HTTP_PARAS);
            paras.put("ip", url.getHost());
            paras.put("port", String.valueOf(port));
            String rsp = HttpClient.send("payback", url.getPath(),
                    HttpUtil.format(map, "utf-8"), null, paras);
            Long packTime = System.currentTimeMillis();
            LOG.info("postback orderNo={}, postBack->{}, use time->{}, rsp->{}", od.orderNo, url.getPath(),
                    (System.currentTimeMillis() - backtime), rsp);
//            if (RspCode.SUCCESS.code.equals(rsp.get(F_rspCode))) {
//                succ = true;
//            } else {
//                LOG.error("payNo=" + od.payNo + "rsp=" + rsp);
//            }
        } catch (Exception e) {
            succ = false;
            LOG.error(e.getMessage());
        }

        if (!succ) {
            // TODO
            // map.put(F_backUrl, order.getBackUrl());
            // Task t = createTask(TaskType.PAY_BACK.type,
            // TaskType.PAY_BACK.desc,
            // JsonUtil.encode(map), new Date().getTime());
            // try {
            // insertTask_Order(conn, t);
            // } catch (Exception e1) {
            // LOG.error(e1.getMessage());
            // }
        }

    }

}
