package com.account.web.controller.test;

import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.AccountDetailDto;
import com.account.service.AccountDetailtService;
import com.account.web.AbstractClientController;
import com.common.annotation.RoleResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(method = {RequestMethod.POST})
public class AccountDetailController extends AbstractClientController {

    @Resource
    private AccountDetailtService accountDetailtService;
    @Resource
    private AccountRPCService accountRPCService;

    /**
     * 查询
     *
     * @param info
     * @return
     */
    @RoleResource(resource = "account")
    @RequestMapping("/accountDetail/list")
    public Map<String, Object> list(@RequestBody AccountDetailDto info) {
        return buildMessage(() -> {
            return accountDetailtService.queryDetailList(info);
        });
    }

    /**
     * 查询
     *
     * @param
     * @return
     */
    @RoleResource(resource = "account")
    @RequestMapping("/account/cco")
    public Map<String, Object> view() {
        return buildMessage(() ->{
                    AccountDetailDto accountDetailDto = new AccountDetailDto();
                    accountDetailDto.setProxyId(1l);
                    return accountRPCService.userChargeAvrge(accountDetailDto);
                }
        );
    }
}
