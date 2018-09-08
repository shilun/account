package com.account.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.passport.rpc.AdminRPCService;
import org.springframework.stereotype.Service;

@Service
public class RPCServiceBean {
    @Reference
    private AdminRPCService adminRPCService;

    public AdminRPCService getAdminRPCService() {
        return adminRPCService;
    }
}
