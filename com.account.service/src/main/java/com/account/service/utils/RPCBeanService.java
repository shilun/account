package com.account.service.utils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.passport.rpc.AdminRPCService;
import com.passport.rpc.ProxyRpcService;
import com.passport.rpc.SMSInfoRPCService;
import com.passport.rpc.UserRPCService;
import org.springframework.stereotype.Component;

@Component
public class RPCBeanService {
    @Reference
    private AdminRPCService adminRPCService;

    @Reference
    private SMSInfoRPCService smsInfoRPCService;

    @Reference
    private ProxyRpcService proxyRpcService;

    @Reference
    private UserRPCService userRPCService;

    @Reference
    private UserRPCService loginRPCServeice;


    public SMSInfoRPCService getSmsInfoRPCService() {
        return smsInfoRPCService;
    }

    public UserRPCService getUserRPCService() {
        return userRPCService;
    }

    public ProxyRpcService getProxyRpcService() {
        return proxyRpcService;
    }

    public AdminRPCService getAdminRPCService() {
        return adminRPCService;
    }

    public UserRPCService getLoginRPCServeice() {
        return loginRPCServeice;
    }
}
