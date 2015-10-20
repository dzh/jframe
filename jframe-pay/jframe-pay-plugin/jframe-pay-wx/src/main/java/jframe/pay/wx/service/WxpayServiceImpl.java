package jframe.pay.wx.service;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.httpclient.service.HttpClientService;
import jframe.pay.dao.service.PayDaoService;
import jframe.pay.domain.PayCurrency;
import jframe.pay.domain.PayStatus;
import jframe.pay.domain.dao.OrderWx;
import jframe.pay.domain.http.RspCode;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.domain.util.ObjectUtil;
import jframe.pay.wx.WxpayPlugin;
import jframe.pay.wx.domain.WxConfig;
import jframe.pay.wx.domain.WxFields;
import jframe.pay.wx.http.client.WxService;
import jframe.pay.wx.http.client.WxServiceNew;

@Injector
public class WxpayServiceImpl implements WxpayService, WxFields {

    static Logger LOG = LoggerFactory.getLogger(WxpayServiceImpl.class);

    @InjectService(id = "jframe.pay.service.dao")
    static PayDaoService PayDao;

    @InjectPlugin
    static WxpayPlugin Plugin;

    @InjectService(id = "jframe.service.httpclient")
    static HttpClientService HttpClient;

    static Map<String, String> HTTP_PARAS = new HashMap<String, String>(1, 1);

    static {
        HTTP_PARAS.put(HttpClientService.P_MIMETYPE, "application/x-www-form-urlencoded");
        // HTTP_PARAS.put(HttpClientService.P_METHOD, "post");
    }

    @Start
    void start() {
        try {
            WxConfig.GroupID = Plugin.getConfig("groupid.wxpay", WxConfig.GroupID);
            WxConfig.init(Plugin.getConfig(WxConfig.CONF_FILE_NAME));
        } catch (Exception e) {
            LOG.error("Load wxconfig error {}", e.getMessage());
        }
    }

    @Stop
    void stop() {

    }

    @Override
    public void pay(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        // check req
        if (HttpUtil.mustReq(req, F_payNo, F_transType, F_payAmount, F_payDesc, F_remoteIp).size() > 0) {
            RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
            return;
        }

        if (PayDao == null) {
            RspCode.setRspCode(rsp, RspCode.FAIL_DB_Conn);
            return;
        }

        // String token = WxUtil.getToken();
        // // TODO 判断有效性的方式 pakcageValue如何设置
        // if (ObjectUtil.isEmpty(token)) {
        // RspCode.setRspCode(rsp, RspCode.FAIL_TOKEN);
        // return;
        // }
        // req.put(F_token, String.valueOf(token));

        if (WxServiceNew.genPrePay(req, rsp)) {
            OrderWx od = PayDao.selectOrderWx(req.get(F_payNo));
            boolean insert = false;
            if (od == null) {
                insert = true;

                od = new OrderWx();
                od.payNo = req.get(F_payNo);
                od.payStatus = PayStatus.C_PAY_WAIT.code;
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
                PayDao.insertOrderWx(od);
            } else
                PayDao.updateOrderWx(od);

            // TODO insert order detail
            // TODO insert task

            RspCode.setRspCode(rsp, RspCode.SUCCESS);
            return;
        }

        rsp.clear();
        RspCode.setRspCode(rsp, RspCode.FAIL_NET);
    }

    @Override
    public void payBack(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        String orderStatus = PayStatus.C_PAY_PROC.code;

        if (WxService.backPay(req, rsp)) {
            orderStatus = PayStatus.C_PAY_SUC.code;
        } else {
            orderStatus = PayStatus.C_PAY_FAIL.code;
        }

        req.put(F_payStatus, orderStatus);
        req.put(F_transactionId, String.valueOf(rsp.get(F_transactionId)));
        wxPayBack(req, rsp);
    }

    @Override
    public void goodReturn(Map<String, String> req, Map<String, Object> rsp) {
        // String[] reqParams = { F_accountFrom, F_accountTo, F_usrTo,
        // F_payGroup,
        // F_payNo, F_transType, F_payAmount };
        // if (!RequestUtil.isValidRequest(req, respMap, reqParams)) {
        // return;
        // }
        //
        // if (_dsSvc == null) {
        // respMap.put(F_respCode, RespCode.FAIL_DB_Conn.code);
        // respMap.put(F_respMsg, RespCode.FAIL_DB_Conn.desc);
        // return;
        // }
        //
        // Connection conn = null;
        // boolean autoCommit = true;
        //
        // try {
        // conn = _dsSvc.getConnection();
        // autoCommit = conn.getAutoCommit();
        // conn.setAutoCommit(false);
        //
        // OrderService orderService = new OrderService();
        // OrderWX pay = orderService
        // .getOrderWXByPayNo(conn, req.get(F_payNo));
        //
        // if (pay == null) {
        // RspCode.setRspCode(rsp, RspCode.FAIL_PAYNO_NOEXIST);
        // return;
        // }
        // if (PayStatus.R_SUC.code.equals(pay.getOrderStatus())) {
        // RspCode.setRspCode(rsp, RspCode.SUCCESS);
        // return;
        // }
        //
        // Map<String, String> resp = new HashMap<String, String>();
        // if (WxService.refund(pay, resp)) {
        // int refundStatus = Integer.valueOf(resp.get("refund_status"));
        // if (refundStatus == 4 || refundStatus == 10) {
        // req.put(F_orderNo, pay.getOrderNum());
        // String prevFlowId = queryOrderFlowId(req);
        // String flowId = IDUtil.genFlowId();
        // // insert flowid
        // this.insertOrderFlow(conn, req, flowId, prevFlowId);
        // // update order
        // this.updateOrder_Cancel(conn, req, resp, flowId, pay);
        //
        // conn.commit();
        // RspCode.setRspCode(rsp, RspCode.SUCCESS);
        // return;
        // } else {
        // LOG.error("orderNum={},refund_status={}",
        // resp.get(F_orderNo), resp.get("refund_status"));
        // }
        // } else {
        // LOG.error("rspCode={},rspDesc={}", resp.get(F_rspCode),
        // resp.get(F_rspDesc));
        // }
        // } catch (Exception e) {
        // LOG.error(e.getMessage());
        // if (conn != null) {
        // try {
        // conn.rollback();
        // } catch (Exception e2) {
        // LOG.error(e2.getMessage());
        // }
        // }
        // } finally {
        // if (conn != null) {
        // try {
        // conn.setAutoCommit(autoCommit);
        // _dsSvc.recycleConnection(conn);
        // } catch (Exception e) {
        // LOG.error(e.getMessage());
        // }
        // }
        // }

        RspCode.setRspCode(rsp, RspCode.FAIL_NET);
    }

    public void wxPayBack(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        String orderNo = req.get(WxFields.F_out_trade_no);
        OrderWx od = PayDao.selectOrderWxWithOrderNo(orderNo);
        if (od == null)
            throw new Exception("OrderWx is not found, orderNo ->" + orderNo);

        if (PayStatus.C_PAY_SUC.code.equals(od.payStatus)) {
            RspCode.setRspCode(rsp, RspCode.SUCCESS);
            return;
        }

        od.tradeMode = Integer.parseInt(req.get(WxFields.F_trade_mode));
        od.payStatus = req.get(WxFields.F_payStatus);
        od.payInfo = req.get(WxFields.F_pay_info);
        od.bankType = req.get(WxFields.F_bank_type);
        od.bankBillNo = req.get(WxFields.F_bank_billno);
        od.notifyId = req.get(WxFields.F_notify_id);
        od.transactionId = req.get(WxFields.F_transactionId);
        od.timeEnd = req.get(F_timestamp);
        od.transportFee = Integer.parseInt(req.getOrDefault(WxFields.F_transport_fee, "0"));
        // transport_fee + product_fee=total_fee
        od.productFee = Integer.parseInt(req.getOrDefault(WxFields.F_product_fee, "0"));
        // 折扣价格,单位分,如果 有值,通知的 total_fee + discount = 请求的 total_fee
        od.discount = Integer.parseInt(req.get(WxFields.F_discount));
        od.buyerAlias = req.get(WxFields.F_buyerAlias);
        od.orderFinishTime = new Date().getTime();

        PayDao.updateOrderWx(od);

        String orderStatus = od.payStatus;
        if (ObjectUtil.notEmpty(od.backUrl) && (PayStatus.C_PAY_SUC.code.equals(orderStatus)
                || PayStatus.C_PAY_FAIL.code.equals(orderStatus) || PayStatus.C_PAY_TIMEOUT.code.equals(orderStatus))) {
            postBack(od);
        } else {
            LOG.error("wx callback orderStatus=" + orderStatus);
        }

    }

    /**
     * 通知租车后台
     * 
     * @param conn
     * @param order
     * @param orderStatus
     */
    public void postBack(OrderWx order) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(F_payNo, order.payNo);
        map.put(F_payStatus, order.payStatus);

        boolean succ = false;
        try {
            URL url = new URL(order.backUrl);
            int port = url.getPort() == -1 ? 80 : url.getPort();

            if (LOG.isDebugEnabled())
                LOG.debug("postBack {},{}", new Date(), map);
            Long packtime = System.currentTimeMillis();

            Map<String, String> paras = new HashMap<>(HTTP_PARAS);
            paras.put("ip", url.getHost());
            paras.put("port", String.valueOf(port));
            Map<String, String> rsp = HttpClient.<HashMap<String, String>> send("payback", url.getPath(),
                    HttpUtil.format(map, "utf-8"), null, paras);
            Long packTime = System.currentTimeMillis();
            if (LOG.isDebugEnabled())
                LOG.debug("orderNo=" + order.orderNo + " postBack" + new Date() + " use time=" + (packTime - packtime)
                        + " rsp=" + rsp);
            if (RspCode.SUCCESS.code.equals(rsp.get(F_rspCode))) {
                succ = true;
            } else {
                LOG.error("payNo=" + order.payNo + "rsp=" + rsp);
            }
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
