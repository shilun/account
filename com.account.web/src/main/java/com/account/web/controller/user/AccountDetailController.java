package com.account.web.controller.user;

import com.account.domain.AccountDetail;
import com.account.domain.module.ChargeTypeEnum;
import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.AccountDetailDto;
import com.account.service.AccountDetailtService;
import com.account.web.AbstractClientController;
import com.common.util.BeanCoper;
import com.common.util.GlosseryEnumUtils;
import com.passport.rpc.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(method = {RequestMethod.POST})
@Api("账户记录")
public class AccountDetailController extends AbstractClientController {

    @Resource
    private AccountDetailtService accountDetailtService;
    @Resource
    private AccountRPCService accountRPCService;

    @RequestMapping("user/accountDetail/list")
    @ApiOperation(value = "查询记录",notes = "bizToken: 1,充值;2,提款;3,赠送 {bizToken:1,pageInfo{page:0,size:30}}")
    public Map<String, Object> list(@RequestBody AccountDetailDto detailDto) {
        return buildMessage(() -> {
            UserDTO userDTO = getUserDto();
            AccountDetail accountDetail = BeanCoper.copyProperties(AccountDetail.class,detailDto);
            accountDetail.setPin(userDTO.getPin());
            Page<AccountDetail> accountDetails = accountDetailtService.queryByPage(accountDetail, detailDto.getPageinfo().getPage());
            List<AccountDetailDto> list = new ArrayList<>();
            for(AccountDetail detail : accountDetails.getContent()){
                AccountDetailDto dto =  BeanCoper.copyProperties(AccountDetailDto.class,detail);
                dto.setChargeTypeName(GlosseryEnumUtils.getItem(ChargeTypeEnum.class,dto.getChargeType()).getName());
                list.add(dto);
            }
            Page<AccountDetailDto> page =  new PageImpl<>(list,detailDto.getPageinfo().getPage(),accountDetails.getTotalElements());
            return page;
        });
    }

    @RequestMapping("accountDetail/list")
    @ApiOperation(value = "查询记录",notes = "bizToken: 1,充值;2,提款;3,赠送 {bizToken:1,pageInfo{page:0,size:30}}")
    public Map<String, Object> accountDetailList(@RequestBody AccountDetailDto detailDto) {
        return buildMessage(() -> {

            return accountRPCService.queryDetail(detailDto);
        });
    }

    @RequestMapping("accountDetail/proxyProfile")
    @ApiOperation(value = "测试代理盈亏统计",notes = "bizToken: 1,充值;2,提款;3,赠送 {bizToken:1,pageInfo{page:0,size:30}}")
    public Map<String, Object> proxyProfile(@RequestBody AccountDetailDto detailDto) {
        return buildMessage(() -> {

            return accountRPCService.queryProxyprofileByDay(detailDto);
        });
    }

    @RequestMapping("accountDetail/new")
    @ApiOperation(value = "测试代理盈亏统计",notes = "bizToken: 1,充值;2,提款;3,赠送 {bizToken:1,pageInfo{page:0,size:30}}")
    public Map<String, Object> newUsers(@RequestBody AccountDetailDto detailDto) {
        return buildMessage(() -> {

            return accountRPCService.queryChargeNewUsersByDay(detailDto);
        });
    }
}
