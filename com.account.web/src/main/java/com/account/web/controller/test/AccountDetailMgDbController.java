package com.account.web.controller.test;

import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.AccountDetailDto;
import com.account.rpc.dto.BizTokenEnum;
import com.account.rpc.dto.BizTypeEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailMgDbService;
import com.account.service.AccountMgDbService;
import com.account.web.AbstractClientController;
import com.common.util.RPCResult;
import com.common.util.model.YesOrNoEnum;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executor;

@RestController
@RequestMapping(method = {RequestMethod.POST})
@Api("测试mgd accountDeatail")
public class AccountDetailMgDbController extends AbstractClientController {
    private org.apache.log4j.Logger logger = Logger.getLogger(AccountDetailMgDbController.class);
    @Resource
    private Executor executor;
    @Resource
    private AccountDetailMgDbService accountDetailMgDbService;
    @Resource
    private AccountMgDbService accountMgDbService;
    @Resource
    private AccountRPCService accountRPCService;

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

    /**
     * 测试执行业务
     *
     * @return
     */
    @ApiOperation(value = "测试多条数据执行")
    @RequestMapping("account/test")
    public Map<String, Object> test() {
        return buildMessage(() -> {
            long start = System.currentTimeMillis();
            List<InvertBizDto> dtos = new ArrayList<>();
            for(int i=1;i<=1000;i++){
                InvertBizDto dto = new InvertBizDto();
                if(i%2==1){
                    dto.setPin("ff0a9a7793994a43bafe62caef90f199");
                }else {
                    dto.setPin("5c6181dbbb9840809501f23d684e6947");
                }
                dto.setAmount(BigDecimal.valueOf(-1));
                dto.setBizType(BizTypeEnum.caipiao.getValue());
                dto.setTokenType(2);
                dto.setBizToken(BizTokenEnum.consume.getValue());
                dto.setTest(YesOrNoEnum.NO.getValue());
                dto.setBizId(i+"_pay");
                dto.setProxyId(1l);
                dtos.add(dto);
            }
            List<List<InvertBizDto>> lists = Lists.partition(dtos, 100);
            for(List<InvertBizDto> dd : lists){
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        RPCResult<List<String>> listRPCResult = null;
                        try{
                            listRPCResult = accountRPCService.invertBizs(dd);
                        }catch (Exception e){
                            logger.error(listRPCResult.getMessage());
                        }
                    }
                });
            }
            long end = System.currentTimeMillis();
            System.out.println("执行"+dtos.size()+"条数据，耗时："+(end-start));
            return null;
        });
    }


}
