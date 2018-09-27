package com.account.web.controller.test;

import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.InvertBizDto;
import com.account.web.AbstractClientController;
import com.account.web.controller.test.dto.ChangeToDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/test", method = {RequestMethod.POST})
public class TestController extends AbstractClientController {

    @Resource
    private AccountRPCService accountRPCService;

    @RequestMapping("test")
    public Map<String, Object> testAccount(InvertBizDto dto) {
        return buildMessage(() -> {
            return accountRPCService.invertBiz(dto);
        });
    }

    @RequestMapping("changeTo")
    public Map<String, Object> changeTo(ChangeToDto dto) {
        return buildMessage(() -> {
            return accountRPCService.changeTo(dto.getProxyId(),dto.getPin(),dto.getSourceType(),dto.getSourceAmount(),dto.getTargetType());
        });
    }
}
