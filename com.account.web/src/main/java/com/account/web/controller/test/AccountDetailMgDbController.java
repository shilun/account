package com.account.web.controller.test;

import com.account.rpc.dto.AccountDetailDto;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailMgDbService;
import com.account.service.AccountMgDbService;
import com.account.web.AbstractClientController;
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
@Api("测试mgd accountDeatail")
public class AccountDetailMgDbController extends AbstractClientController {

    @Resource
    private AccountDetailMgDbService accountDetailMgDbService;
    @Resource
    private AccountMgDbService accountMgDbService;

    /**
     * 平台平均充值量
     *
     * @return
     */
    @ApiOperation(value = "平台平均充值量")
    @RequestMapping("accountDetail/avargCharge")
    public Map<String, Object> avargCharge(@RequestBody AccountDetailDto dto) {
        return buildMessage(() -> {
            return accountDetailMgDbService.avargCharge(dto);
        });
    }

    /**
     * 根据时间查询充值人数 dayStatus 1 全部 2 当天  3 本周 4 本月
     *
     * @return
     */
    @ApiOperation(value = "根据时间查询充值人数")
    @RequestMapping("accountDetail/chargeUsers")
    public Map<String, Object> queryChargeUsers(@RequestBody AccountDetailDto dto) {
        return buildMessage(() -> {
            return accountDetailMgDbService.queryChargeUsers(dto);
        });
    }

    /**
     * 根据时间查询充值总额 dayStatus1 全部 2 当天  3 本周 4 本月
     *
     * @return
     */
    @ApiOperation(value = "根据时间查询充值总额")
    @RequestMapping("accountDetail/chargeAmount")
    public Map<String, Object> queryChargeAmount(@RequestBody AccountDetailDto dto) {
        return buildMessage(() -> {
            return accountDetailMgDbService.queryChargeAmount(dto);
        });
    }

    /**
     * 根据时间查询新增充值人数 dayStatus 1 全部 2 当天  3 本周 4 本月
     *
     * @return
     */
    @ApiOperation(value = "根据时间查询新增充值人数")
    @RequestMapping("accountDetail/chargeNewUsers")
    public Map<String, Object> queryChargeNewUsers(@RequestBody AccountDetailDto dto) {
        return buildMessage(() -> {
            return accountDetailMgDbService.queryChargeNewUsers(dto);
        });
    }

    /**
     * 根据时间查询代理商盈亏数据 dayStatus1 全部 2 当天  3 本周 4 本月
     *
     * @return
     */
    @ApiOperation(value = "根据时间查询代理商盈亏数据")
    @RequestMapping("accountDetail/queryProxyProfile")
    public Map<String, Object> queryProxyProfile(@RequestBody AccountDetailDto dto) {
        return buildMessage(() -> {
            return accountDetailMgDbService.queryProxyProfile(dto);
        });
    }

    /**
     * 测试执行业务
     *
     * @return
     */
    @ApiOperation(value = "测试执行业务")
    @RequestMapping("account/newBiz")
    public Map<String, Object> newBiz(@RequestBody InvertBizDto dto) {
        return buildMessage(() -> {
            accountMgDbService.newBiz(dto);
            return null;
        });
    }
}
