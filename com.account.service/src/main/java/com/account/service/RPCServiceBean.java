package com.account.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.passport.rpc.AdminRPCService;
import com.passport.rpc.ProxyRpcService;
import org.springframework.stereotype.Service;

@Service
public class RPCServiceBean {
    @Reference
    private AdminRPCService adminRPCService;

    @Reference
    private ProxyRpcService proxyRpcService;
    public AdminRPCService getAdminRPCService() {
        return adminRPCService;
    }

    public ProxyRpcService getProxyRpcService() {
        return proxyRpcService;
    }
}
