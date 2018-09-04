package com.account.web.controller;

import com.account.domain.Account;
import com.account.service.AccountService;
import com.account.web.AbstractClientController;
import com.account.web.controller.dto.AccountDto;
import com.common.annotation.RoleResource;
import com.common.util.BeanCoper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(method = {RequestMethod.POST})
public class AccountController extends AbstractClientController {

    @Resource
    private AccountService accountService;

    /**
     * 查询
     *
     * @param info
     * @return
     */
    @RoleResource(resource = "account")
    @RequestMapping("/account/list")
    public Map<String, Object> list(@RequestBody AccountDto info) {
        return buildMessage(() -> {
            Account query = new Account();
            BeanCoper.copyProperties(query, info);
            return accountService.queryByPage(query, info.getPageinfo().getPage());
        });
    }

    /**
     * 查询
     *
     * @param content
     * @return
     */
    @RoleResource(resource = "account")
    @RequestMapping("/account/view")
    public Map<String, Object> view(@RequestBody String content) {
        return buildMessage(() ->
                accountService.findById(getIdByJson(content)));
    }


}
