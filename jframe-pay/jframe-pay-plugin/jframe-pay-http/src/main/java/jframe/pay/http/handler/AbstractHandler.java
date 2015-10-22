/**
 * 
 */
package jframe.pay.http.handler;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.serializer.SerializeFilter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import jframe.core.msg.Msg;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.pay.domain.Fields;
import jframe.pay.domain.http.RspCode;
import jframe.pay.domain.util.HttpUtil;
import jframe.pay.domain.util.JsonUtil;
import jframe.pay.http.HttpConstants;
import jframe.pay.http.PayHttpPlugin;

/**
 * TODO dispose
 * 
 * @author dzh
 * @date Jul 25, 2014 11:23:15 AM
 * @since 1.0
 */
@Injector
public abstract class AbstractHandler extends SimpleChannelInboundHandler<HttpObject> {

    static Logger LOG = LoggerFactory.getLogger(AbstractHandler.class);

    static Logger LOG_REQ = LoggerFactory.getLogger("jframe.pay.http.handler.req");

    @InjectPlugin
    protected static PayHttpPlugin Plugin;

    protected boolean keepAlive = false;

    protected boolean gzip = false;

    protected Map<String, Object> rspMap;

    protected ChannelHandlerContext ctx;

    protected AbstractHandler() {
        this.rspMap = initRespMap();
    }

    private Map<String, List<String>> _params;

    private StringBuilder data;

    // private ByteBufOutputStream data;

    protected HttpRequest req;

    protected void putRspMap(String key, Object value) {
        rspMap.put(key, value);
    }

    /**
     * 
     */
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            // TODO
        }
        return super.acceptInboundMessage(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.
     * channel .ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (this.ctx == null)
            this.ctx = ctx;

        if (msg instanceof HttpRequest) {
            readHttpRequest((HttpRequest) msg);
            if (LOG_REQ.isDebugEnabled()) {
                LOG_REQ.debug("Start reqUrl->{},ip->{},date->{}", getReqUrl(), getRemoteIp(), new Date().getTime());
            }
        } else if (msg instanceof HttpContent) {
            readHttpContent((HttpContent) msg);
        }
    }

    public String getRemoteIp() {
        // nginx时设置客户端真实ip
        String remoteIp = getHttpRequest().headers().get("X-Real-Ip");
        if (Objects.isNull(remoteIp)) {
            remoteIp = ((InetSocketAddress) getChannelHandlerContext().channel().remoteAddress()).getHostName();
        }
        return remoteIp;
    }

    protected void readHttpRequest(HttpRequest msg) throws Exception {
        HttpRequest req = (HttpRequest) msg;
        AbstractHandler.this.req = req;

        if (!req.getMethod().equals(HttpMethod.POST) || !isValidHeaders(req.headers())) {
            finish(ctx);
            return;
        }

        keepAlive = HttpHeaders.isKeepAlive(req);
        String encoding = req.headers().get(HttpHeaders.Names.ACCEPT_ENCODING);
        if (encoding != null && encoding.indexOf("gzip") != -1) {
            gzip = true;
        }
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.getUri());
        _params = queryStringDecoder.parameters();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Receive request -> {}", req.getUri());
        }
    }

    // TODO buf
    protected void readHttpContent(HttpContent msg) throws Exception {
        StringBuilder buf = data == null ? data = new StringBuilder() : data;
        buf.append(msg.content().toString(CharsetUtil.UTF_8));
        if (msg instanceof LastHttpContent) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Dispatch req map {}",
                            parseHttpReq(URLDecoder.decode(buf.toString(), HttpConstants.UTF8)));
                }

                String content = URLDecoder.decode(buf.toString(), HttpConstants.UTF8);
                if (isValidData(content)) {
                    service(parseHttpReq(content), rspMap);
                } else {
                    RspCode.setRspCode(rspMap, RspCode.FAIL_SIGN_ERROR);
                }
            } finally {
                finish(ctx);
            }
            if (LOG_REQ.isDebugEnabled()) {
                LOG_REQ.debug("Finish reqUrl->{},ip->{},date->{}", getReqUrl(), getRemoteIp(), new Date().getTime());
            }
        }
    }

    protected boolean isValidData(String data) {

        return Objects.nonNull(data);
    }

    /**
     * 请求接口版本 TODO
     * 
     * @return
     */
    public String getPayVern() {
        return req.headers().get("pay-vern");
    }

    /**
     * 任何一个url的最后一段是请求码
     * 
     * @return
     */
    protected String getReqOp() {
        String[] path = req.getUri().substring(1).split("\\/");
        String reqOp = path.length == 0 ? "" : path[path.length - 1];
        int loc = reqOp.indexOf('?');
        if (loc != -1) {
            return reqOp.substring(0, loc);
        }
        return reqOp;
    }

    protected String getReqUrl() {
        String reqUrl = req.getUri();
        int loc = reqUrl.indexOf('?');
        if (loc != -1) {
            return reqUrl.substring(0, loc);
        }
        return reqUrl;
    }

    protected String[] getReqPath() {
        return req.getUri().substring(1).split("\\/");
    }

    protected void finish(ChannelHandlerContext ctx) throws Exception {
        Map<String, Object> resp = null;
        try {
            resp = filterRspMap(rspMap);
        } finally {
            writeResponse(ctx, resp);
            ctx.pipeline().remove(this);
            if (data != null) {
                // data.close();
                data = null;
            }
        }
    }

    public HttpRequest getHttpRequest() {
        return req;
    }

    public HttpHeaders getHttpHeaders() {
        if (req != null)
            return req.headers();
        return null;
    }

    /**
     * @param headers
     * @return
     */
    public boolean isValidHeaders(HttpHeaders headers) {
        return true;
    }

    protected Map<String, String> parseHttpReq(String content) throws Exception {
        // if (content.indexOf('=') != -1) {
        // return parseHttpParas(content);
        // }
        // return JsonUtil.decode(content);
        Map<String, String> req = HttpUtil.parseHttpParas(content.trim());
        req.put("reqUrl", getReqUrl());
        return req;
    }

    /**
     * @param respMap2
     * @return
     */
    protected Map<String, Object> filterRspMap(Map<String, Object> rsp) {
        return rsp;
    }

    public List<String> getReqPara(String key) {
        List<String> l = _params.get(key);
        if (l == null)
            return Collections.emptyList();
        return l;
    }

    protected Map<String, List<String>> getPara() {
        return _params;
    }

    /**
     * 
     * @param key
     * @param defVal
     *            默认值
     * @return
     */
    public String getReqPara(String key, String defVal) {
        List<String> l = _params.get(key);
        if (l != null && l.size() > 0)
            return l.get(0);
        return defVal;
    }

    /**
     * @param req
     * @param resp
     */
    public abstract void service(Map<String, String> req, Map<String, Object> rsp) throws PayException;

    protected Map<String, Object> initRespMap() {
        return new HashMap<String, Object>();
    }

    protected void writeResponse(ChannelHandlerContext ctx, Map<String, Object> rsp) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("response {}", rsp);
        }

        FullHttpResponse response = createHttpResponse(rsp);
        ChannelFuture future = ctx.writeAndFlush(response);
        if (!keepAlive) {
            LOG.info("close channel {}", this.getReqUrl());
            future.addListener(ChannelFutureListener.CLOSE);
        }
        // future.channel().close();
        // TODO
    }

    protected FullHttpResponse createHttpResponse(Map<String, Object> rsp) {
        FullHttpResponse response = null;
        if (rsp == null || rsp.isEmpty()) {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        } else {
            if (rsp.containsKey(Fields.F_rspOut)) {
                response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                        Unpooled.copiedBuffer(rsp.get(Fields.F_rspOut).toString(), CharsetUtil.UTF_8));
            } else {
                Object filter = rsp.remove(Fields.F_filter);
                if (filter != null && filter instanceof SerializeFilter) {
                    response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                            Unpooled.copiedBuffer(JsonUtil.encode(rsp, (SerializeFilter) filter), CharsetUtil.UTF_8));
                } else {
                    response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                            Unpooled.copiedBuffer(JsonUtil.encode(rsp), CharsetUtil.UTF_8));
                }
            }
        }

        if (gzip) {
            // response.headers().set(CONTENT_ENCODING, "gzip");
        }
        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        return response;
    }

    public void sendMsg(Msg<?> msg) {
        Plugin.send(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        LOG.error(cause.getMessage(), cause);
        finish(ctx);
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return ctx;
    }
}
