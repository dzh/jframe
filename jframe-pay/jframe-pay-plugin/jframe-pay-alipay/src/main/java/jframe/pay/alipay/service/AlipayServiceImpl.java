/**
 * 
 */
package jframe.pay.alipay.service;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.httpclient.service.HttpClientService;
import jframe.pay.alipay.AlipayConfig;
import jframe.pay.alipay.AlipayPlugin;
import jframe.pay.alipay.domain.TradeStatus;
import jframe.pay.dao.service.PayDaoService;
import jframe.pay.domain.Fields;
import jframe.pay.domain.PayCurrency;
import jframe.pay.domain.PayStatus;
import jframe.pay.domain.TransType;
import jframe.pay.domain.dao.OrderAlipay;
import jframe.pay.domain.http.RspCode;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.domain.util.IDUtil;
import jframe.pay.domain.util.ObjectUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.util.OrderUtil;

/**
 * @author dzh
 * @date Nov 26, 2014 4:39:26 PM
 * @since 1.0
 */
@Injector
class AlipayServiceImpl implements AlipayService, Fields {

    static Logger LOG = LoggerFactory.getLogger(AlipayServiceImpl.class);

    @InjectService(id = "jframe.pay.service.dao")
    static PayDaoService PayDao;

    @InjectPlugin
    static AlipayPlugin Plugin;

    @InjectService(id = "jframe.service.httpclient")
    static HttpClientService HttpClient;

    static Map<String, String> HTTP_PARAS = new HashMap<String, String>(1, 1);

    static {
        HTTP_PARAS.put(HttpClientService.P_MIMETYPE, "application/x-www-form-urlencoded");
        // HTTP_PARAS.put(HttpClientService.P_METHOD, "post");
    }

    @Start
    public void start() {
        try {
            AlipayConfig.GroupID = Plugin.getConfig("groupid.alipay", AlipayConfig.GroupID);
            AlipayConfig.init(Plugin.getConfig("file.alipay"));
        } catch (Exception e) {
            LOG.error("Load alipay error {}", e.getMessage());
        }
    }

    @Stop
    public void stop() {

    }

    @Override
    public void pay(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        // check req
        if (HttpUtil.mustReq(req, F_payType, F_payGroup, F_payNo, F_transType, F_payAmount, F_payDesc).size() > 0) {
            RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
            return;
        }

        if (PayDao == null) {
            RspCode.setRspCode(rsp, RspCode.FAIL_DB_Conn);
            return;
        }
        Optional<OrderAlipay> od = Optional.ofNullable(PayDao.selectOrderAlipay(req.get(F_payNo)));
        if (od.isPresent()) {
            payUpdate(od.get(), req, rsp);
        } else {
            payNew(req, rsp);
        }
    }

    private void payNew(Map<String, String> req, Map<String, Object> rsp) {
        OrderAlipay od = new OrderAlipay();
        if (req.containsKey(F_account))
            od.account = req.get(F_account);
        od.backUrl = req.get(F_backUrl);
        od.orderCreateTime = new Date().getTime();
        od.orderNo = IDUtil.genOrderNo();
        od.payAmount = Long.parseLong(req.getOrDefault(F_payAmount, "0"));
        od.payCurrency = req.getOrDefault(F_payCurrency, PayCurrency.CNY.code);
        od.payDesc = req.getOrDefault(F_payDesc, "");
        od.payGroup = req.getOrDefault(F_payGroup, "");
        od.payNo = req.get(F_payNo);
        od.payTimeout = new Date().getTime() + 3600 * 1000;
        od.transType = TransType.Consume.code;

        req.put(F_orderNo, od.orderNo);
        if (od.payAmount > 0) {
            od.payStatus = PayStatus.C_PAY_WAIT.code;
        } else {
            od.orderFinishTime = new Date().getTime();
            od.payStatus = PayStatus.C_PAY_SUC.code;
            PayDao.insertOrderAlipay(od);
            return;
        }
        // insert order
        PayDao.insertOrderAlipay(od);

        // pay info
        String odinfo = OrderUtil.buildOrderInfo(req);
        rsp.put(F_od, OrderUtil.genPayInfo(odinfo));

        // Map<String, String> map = new HashMap<>();
        // map.put("out_trade_no", req.get(F_orderNo));

        // insert task TODO
        // Task t = createTask(TaskType.ALI_QUERY_C_PAY.type,
        // TaskType.ALI_QUERY_C_PAY.desc, JsonUtil.encode(map),
        // new Date().getTime());
        // TaskDao.insertTask_Order(conn, t);
    }

    private void payUpdate(OrderAlipay od, Map<String, String> req, Map<String, Object> rsp) {
        if (HttpUtil.anyEq(od.payStatus, PayStatus.C_PAY_SUC.code, PayStatus.C_PAY_TIMEOUT.code)) {
            RspCode.setRspCode(rsp, RspCode.FAIL_ORDER_STATUS);
            return;
        }
        od.backUrl = req.get(F_backUrl);
        od.orderNo = IDUtil.genOrderNo();
        od.payAmount = Long.parseLong(req.getOrDefault(F_payAmount, "0"));
        od.payCurrency = req.getOrDefault(F_payCurrency, PayCurrency.CNY.code);
        od.payDesc = req.getOrDefault(F_payDesc, "");
        od.payGroup = req.getOrDefault(F_payGroup, "");
        od.payNo = req.get(F_payNo);
        od.payTimeout = new Date().getTime() + 3600 * 1000;
        od.transType = TransType.Consume.code;

        req.put(F_orderNo, od.orderNo);
        if (od.payAmount > 0) {
            od.payStatus = PayStatus.C_PAY_WAIT.code;
        } else {
            od.orderFinishTime = new Date().getTime();
            od.payStatus = PayStatus.C_PAY_SUC.code;
            PayDao.updateOrderAlipay(od);
            return;
        }
        // pay info
        String odinfo = OrderUtil.buildOrderInfo(req);
        if (LOG.isDebugEnabled()) {
            LOG.debug("odinfo -> {}", odinfo);
        }
        rsp.put(F_od, OrderUtil.genPayInfo(odinfo));

        // Map<String, String> map = new HashMap<>();
        // map.put("out_trade_no", req.get(F_orderNo));
        // insert task TODO
    }

    @Override
    public void payBack(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        // String out_trade_no = req.get("out_trade_no");
        // 支付宝交易号
        // String trade_no = req.get("trade_no");
        // 交易状态
        String trade_status = req.get("trade_status");
        // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        // if (AlipayNotify.verify(req)) {// 验证成功
        // Test Code

        /*
         * LOG.info("支付宝验签请求："+JSON.toJSONString(req)); if
         * (AlipayNotify.verify(req)) { LOG.info("支付宝验签成功"); }else{
         * LOG.info("支付宝验签失败"); }
         */

        if (true) {
            if (trade_status.equals(TradeStatus.TRADE_FINISHED)) {
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 如果有做过处理，不执行商户的业务程序

                // 注意：
                // 该种交易状态只在两种情况下出现
                // 1、开通了普通即时到账，买家付款成功后。
                // 2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
                doSuccess(req, rsp);
            } else if (trade_status.equals(TradeStatus.TRADE_SUCCESS)) {
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 如果有做过处理，不执行商户的业务程序

                // 注意：
                // 该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
                doSuccess(req, rsp);
            } else if (trade_status.equals(TradeStatus.WAIT_BUYER_PAY)) {
                // 交易创建
                // doWaitPay(req, resp);
            } else {
                RspCode.setRspCode(rsp, RspCode.FAIL_ALIPAY_BACK_UNKOWN_STATUS_ERROR);
            }
            return;
        }
        RspCode.setRspCode(rsp, RspCode.FAIL_ALIPAY_BACK_SIGN_ERROR);
    }

    private void doWaitPay(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        String orderNo = req.get("out_trade_no");
        OrderAlipay od = PayDao.selectOrderAlipayWithOrderNo(req.get("out_trade_no"));
        if (od == null)
            throw new Exception("OrderAlipay is not found, orderNo ->" + orderNo);
        if (PayStatus.C_PAY_PROC.code.equals(od.payStatus)) {
            RspCode.setRspCode(rsp, RspCode.SUCCESS);
            return;
        }

        // update order
        od.payStatus = PayStatus.C_PAY_PROC.code;
        PayDao.updateOrderAlipay(od);

        // insert flowid
        // String prevFlowId = order.getOrderFlowId();
        // String flowId = IDUtil.genFlowId();
        // req.put(UsrFields.F_payNo, order.getPayNo());
        // req.put(UsrFields.F_orderStatus, orderStatus);
        // OrderDao.insertOrderFlow(conn, req, flowId, prevFlowId);

        // AlipayDao.updateOrder_Back(conn, req, orderStatus, "", flowId);
    }

    private void doSuccess(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        String orderNo = req.get("out_trade_no");
        OrderAlipay od = PayDao.selectOrderAlipayWithOrderNo(orderNo);
        if (od == null)
            throw new Exception("OrderAlipay is not found, orderNo ->" + orderNo);

        if (PayStatus.C_PAY_SUC.code.equals(od.payStatus)) {
            RspCode.setRspCode(rsp, RspCode.SUCCESS);
            return;
        }

        // TODO
        od.payStatus = PayStatus.C_PAY_SUC.code;
        od.notifyId = req.get("notify_id");
        od.notifyType = req.get("notify_type");
        od.tradeStatus = req.get("trade_status");
        od.subject = req.get("subject");
        od.sellerId = req.get("seller_id");
        od.sellerEmail = req.get("seller_email");
        od.buyerId = req.get("buyer_id");
        od.buyerEmail = req.get("buyer_email");
        od.orderFinishTime = new Date().getTime();
        od.notifyTime = od.orderFinishTime;
        PayDao.updateOrderAlipay(od);

        // insert flowid
        // String prevFlowId = order.getOrderFlowId();
        // String flowId = IDUtil.genFlowId();
        // req.put(UsrFields.F_payNo, order.getPayNo());
        // req.put(UsrFields.F_orderStatus, orderStatus);
        // OrderDao.insertOrderFlow(conn, req, flowId, prevFlowId);

        // String accmount_flowId = null;
        // insert amount
        // accmount_flowId = OrderDao.opAccount(conn, order, "alpay");
        // update order
        // AlipayDao.updateOrder_Back(conn, req, orderStatus, accmount_flowId,
        // flowId);
        // resp.put(UsrFields.F_respCode, URespCode.SUCCESS.code);

        if (ObjectUtil.notEmpty(od.backUrl)) {
            postBack(od);
        }
    }

    /**
     * TODO
     * 
     * @param usrid
     * @param accountid
     * @return
     */
    public boolean accountExist(String usrid, String accountid) {
        // MemcachedService memSvc = this.getMemSvc();
        // if (memSvc != null) {
        // Object acc = memSvc.get(ServiceConstants.Key_Account_Prefix
        // + accountid);
        // if (acc != null)
        // return true;
        // }

        // AccountService accountServlet = new AccountService();
        // Connection conn = null;
        // try {
        // conn = _dsSvc.getConnection();
        // Account account = accountServlet.getAmountByUserAndId(conn, usrid,
        // accountid);
        // if (account != null) {
        // // if (memSvc != null) {
        // // memSvc.set(ServiceConstants.Key_Account_Prefix + accountid,
        // // JsonUtil.toJson(account),
        // // new Date(12 * 3600 * 1000));
        // // }
        // return true;
        // }
        // } catch (Exception e) {
        // LOG.error(e.getMessage());
        // } finally {
        // if (conn != null && _dsSvc != null) {
        // try {
        // _dsSvc.recycleConnection(conn);
        // } catch (SQLException e) {
        // }
        // }
        // }
        return true;
    }

    public void postBack(OrderAlipay order) {
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
            // map.put(UsrFields.F_backUrl, order.getBackUrl());
            // Task t = createTask(TaskType.PAY_BACK.type,
            // TaskType.PAY_BACK.desc,
            // JsonUtil.encode(map), new Date().getTime());
            // try {
            // AlipayDao.insertTask_Order(conn, t);
            // } catch (Exception e1) {
            // LOG.error(e1.getMessage());
            // }
        }

    }

    // Task createTask(int type, String name, String content, long startTime) {
    // Task t = new TaskImpl();
    // t.setType(type);
    // t.setStartTime(startTime);
    // t.setName(name);
    // t.setStatus(TaskStatus.START.status);
    // t.setContent(content);
    // return t;
    // }

    @Override
    public void clientPayBack(Map<String, String> req, Map<String, Object> rsp) throws Exception {
        // check req
        if (HttpUtil.mustReq(req, F_payNo).size() > 0) {
            RspCode.setRspCode(rsp, RspCode.FAIL_HTTP_MISS_PARA);
            return;
        }

        if (PayDao == null) {
            RspCode.setRspCode(rsp, RspCode.FAIL_DB_Conn);
            return;
        }

        OrderAlipay od = PayDao.selectOrderAlipay(req.get(F_payNo));
        // req.put("out_trade_no", order.orderNo);
        // req.put(F_payNo, order.payNo);
        // req.put(F_payStatus, PayStatus.C_PAY_SUC.code);

        od.payStatus = req.getOrDefault(F_payStatus, PayStatus.C_PAY_SUC.code);
        PayDao.updateOrderAlipay(od);

        String accmount_flowId = null;
        // TODO insert amount
        // accmount_flowId = OrderDao.opAccount(conn, order, "alpay");
        // update order
        // AlipayDao.updateOrder_Back(conn, req, orderStatus, accmount_flowId,
        // flowId);
    }

}
