/**
 * 
 */
package jframe.pay.http.handler;

import java.util.Map;

import jframe.pay.domain.Fields;
import jframe.pay.domain.http.RspCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Jul 29, 2015 2:09:06 PM
 * @since 1.0
 */
public abstract class PayHandler extends SafeHandler {

    public static final Logger LOG = LoggerFactory.getLogger(PayHandler.class);

    @Override
    public void service(Map<String, String> req, Map<String, Object> rsp) throws PayException {
        super.service(req, rsp);

        long start = System.currentTimeMillis();
        try {
            doService(req, rsp);

            if (!rsp.containsKey(Fields.F_rspCode)) {
                RspCode.setRspCode(rsp, RspCode.SUCCESS);
            }
        } catch (Exception e) {
            if (!rsp.containsKey(Fields.F_rspCode)) {
                RspCode.setRspCode(rsp, RspCode.FAIL_NET);
            }

            LOG.error(e.getMessage(), e.getCause());

            if (isDebug()) {
                rsp.put(Fields.F_error, e.getMessage());
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("remoteIp->{} reqOp->{} invokeTime->{}ms req->{} rsp->{}", getRemoteIp(), getReqOp(),
                    System.currentTimeMillis() - start, req, rsp);
        }
    }

    protected abstract void doService(Map<String, String> req, Map<String, Object> rsp) throws Exception;

    public boolean isDebug() {
        return "true".equals(Plugin.getConfig("jframe.debug", "false")) ? true : false;
    }

}
