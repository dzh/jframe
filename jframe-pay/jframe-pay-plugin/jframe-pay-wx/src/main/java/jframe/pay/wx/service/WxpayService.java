package jframe.pay.wx.service;

import java.util.Map;

import jframe.core.plugin.annotation.Service;

@Service(clazz = "jframe.pay.wx.service.WxpayServiceImpl", id = "jframe.pay.service.wxpay")
public interface WxpayService {

    void pay(Map<String, String> req, Map<String, Object> rsp) throws Exception;

    void payBack(Map<String, String> req, Map<String, Object> rsp)
            throws Exception;

    void goodReturn(Map<String, String> req, Map<String, Object> rsp)
            throws Exception;

}
