package com.account.web.controller.user;

import com.account.domain.AccountDetail;
import com.account.rpc.dto.AccountDetailDto;
import com.account.service.AccountDetailtService;
import com.account.web.AbstractClientController;
import com.common.util.BeanCoper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(method = {RequestMethod.POST})
@Api("账户记录")
public class AccountDetailController extends AbstractClientController {

    @Resource
    private AccountDetailtService accountDetailtService;

    @RequestMapping("user/accountDetail/list")
    @ApiOperation(value = "查询记录",notes = "bizToken: 1,充值;2,提款;3,赠送 {bizToken:1,pageInfo{page:0,size:30}}")
    public Map<String, Object> addUserBank(@RequestBody AccountDetailDto detailDto) {
        return buildMessage(() -> {
            AccountDetail accountDetail = BeanCoper.copyProperties(AccountDetail.class,detailDto);

            return accountDetailtService.queryByPage(accountDetail,detailDto.getPageinfo().getPage());
        });
    }
}
