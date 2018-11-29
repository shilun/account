package com.account.service.rpc;

import com.account.domain.*;
import com.account.rpc.dto.BizTypeEnum;
import com.account.domain.module.DetailStatusEnum;
import com.account.domain.module.TokenTypeEnum;
import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.*;
import com.account.service.*;
import com.account.service.utils.TimeUtils;
import com.common.exception.BizException;
import com.common.util.BeanCoper;
import com.common.util.DateUtil;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.common.util.model.YesOrNoEnum;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@com.alibaba.dubbo.config.annotation.Service(interfaceClass=AccountRPCService.class)
public class AccountRPCServiceImpl implements AccountRPCService {

    private Logger logger = Logger.getLogger(AccountRPCServiceImpl.class);


    @Resource
    private AccountMgDbService accountMgDbService;

    @Resource
    private AccountDetailMgDbService accountDetailMgDbService;
    
    @Resource
    private ConfigService configService;

    @Resource
    private UserDrawalLogService userDrawalLogService;

    @Resource
    private UserDrawalPassService userDrawalPassService;

    @Resource
    private UserBankService userBankService;

    @Resource
    private WithdrawCfgInfoService  withdrawCfgInfoService;


    @Override
    public RPCResult<List<AccountDto>> queryAccountWithRate(String pin, Long proxyId) {
        RPCResult<List<AccountDto>> result = null;
        try {
            List<AccountDto> resultlist = getAccountDtos(pin, proxyId);
            result = new RPCResult<>(resultlist);
            return result;
        } catch (Exception e) {
            logger.error("查询账户失败", e);
            result.setSuccess(false);
        }
        result.setMessage("查询账户失败");
        result.setCode("account.queryAccount.error");
        return result;
    }

    @Override
    public RPCResult<List<AccountDto>> queryAccount(String pin, Long proxyId) {
        RPCResult<List<AccountDto>> result = new RPCResult<>();
        try {
            List<AccountDto> resultlist = getAccountDtos(pin, proxyId);
            result.setSuccess(true);
            result.setData(resultlist);
            return result;
        } catch (Exception e) {
            logger.error("查询账户失败", e);
            result.setSuccess(false);
        }
        result.setMessage("查询账户失败");
        result.setCode("account.queryAccount.error");
        return result;
    }


    @Override
    public RPCResult<List<AccountDto>> queryAccounts(Long proxyId, Integer tokenType, Integer pageIndex, Integer pageSize) {
        Account query = new Account();
        query.setProxyId(proxyId);
        query.setTokenType(tokenType);
        RPCResult<List<AccountDto>> result = new RPCResult<>();
        try {
            List<AccountDto> resultList = new ArrayList<>();
            Page<Account> accounts = accountMgDbService.queryByPage(query, new PageRequest(pageIndex, pageSize));
            result.setSuccess(true);
            result.setPageIndex(pageIndex);
            result.setPageSize(pageSize);
            result.setTotalCount(accounts.getNumberOfElements());
            result.setTotalPage(accounts.getTotalPages());
            for (Account item : accounts.getContent()) {
                AccountDto accountDto = BeanCoper.copyProperties(AccountDto.class, item);
                resultList.add(accountDto);
            }
            result.setData(resultList);
            return result;
        } catch (Exception e) {
            logger.error("查询账户失败", e);
            result.setSuccess(false);
        }
        result.setMessage("查询账户失败");
        result.setCode("account.queryAccount.error");
        return result;
    }

    private List<AccountDto> getAccountDtos(String pin, Long proxyId) {
        Account query = new Account();
        query.setProxyId(proxyId);
        query.setPin(pin);
        List<Account> list = accountMgDbService.query(query);
        for (TokenTypeEnum type : TokenTypeEnum.values()) {
            boolean moneyed = false;
            for (Account item : list) {
                if (type.getValue().intValue() == item.getTokenType()) {
                    moneyed = true;
                    break;
                }
            }
            if (!moneyed) {
                Account e = new Account();
                e.setPin(pin);
                e.setTokenType(type.getValue());
                e.setProxyId(proxyId);
                e.setAmount(BigDecimal.ZERO);
                e.setFreeze(BigDecimal.ZERO);
                e.setTokenType(type.getValue());
                list.add(e);
            }
        }
        List<AccountDto> resultlist = new ArrayList<>();
        for (Account item : list) {
            AccountDto dto = new AccountDto();
            BeanCoper.copyProperties(dto, item);
            dto.setTokenType(item.getTokenType());
            resultlist.add(dto);
        }
        return resultlist;
    }

    @Override

    public RPCResult<Boolean> invertBiz(InvertBizDto dto) {
        RPCResult result = new RPCResult<>();
        try {
            accountMgDbService.newBiz(dto);
            result.setSuccess(true);
            return result;
        } catch (BizException e) {
            result.setCode(e.getCode());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("执行业务失败", e);
        }
        result.setCode("account.invertBiz.error");
        result.setMessage("执行业务失败");
        result.setSuccess(false);
        return result;
    }

    @Override
    public RPCResult<List<String>> invertBizs(List<InvertBizDto> dtos) {
        RPCResult result = new RPCResult();
        List<String> faildList = new ArrayList<>();
        for (InvertBizDto dto : dtos) {
            try {
                accountMgDbService.newBiz(dto);
            } catch (Exception e) {
                logger.error("执行业务失败", e);
                faildList.add(dto.getBizId());
            }
        }
        result.setSuccess(true);
        result.setData(faildList);
        return result;
    }

    @Override
    public RPCResult changeTo(Long proxyId, String pin, Integer sourceType, BigDecimal sourceAmount, Integer targetType) {
        RPCResult result = new RPCResult();
        try {
            accountDetailMgDbService.changeTo(proxyId, pin, sourceType, sourceAmount, targetType);
            result.setSuccess(true);
            return result;
        } catch (BizException e) {
            result.setCode(e.getCode());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("AccountRPCServiceImpl.changeTo.error", e);
            result.setSuccess(false);
            result.setCode("changeTo.error");
            result.setMessage("转账失败");
            return result;
        }
    }

//
//    @Override
//    public RPCResult<Boolean> invertBizBack(BizTypeEnum bizType, String bizId) {
//        RPCResult result = new RPCResult();
//        try {
//            rollBackBiz(bizType, bizId);
//            result.setSuccess(true);
//            return result;
//        } catch (Exception e) {
//            logger.error("业务回滚失败", e);
//        }
//        result.setSuccess(false);
//        result.setCode("account.invertBizBack.error");
//        result.setMessage("业务回滚失败");
//        return result;
//    }

    @Transactional
    public void rollBackBiz(BizTypeEnum bizType, String bizId) {
        AccountDetail query = new AccountDetail();
        query.setBizType(bizType.getValue());
        query.setBizId(bizId);
        AccountDetail detail = accountDetailMgDbService.findByOne(query);
        Account queryAccount = new Account();
        queryAccount.setPin(detail.getPin());
        queryAccount.setTokenType(detail.getTokenType());
        Account account = accountMgDbService.findByOne(queryAccount);
        if (detail.getChangeAmount().compareTo(BigDecimal.ZERO) > 0) {
            account.setAmount(account.getAmount().subtract(detail.getChangeAmount()));
        }
        if (detail.getChangeFreeze().compareTo(BigDecimal.ZERO) > 0) {
            account.setFreeze(account.getFreeze().subtract(detail.getChangeFreeze()));
        }
        if (detail.getChangeAmount().compareTo(BigDecimal.ZERO) < 0) {
            account.getAmount().add(detail.getChangeAmount());
        }
        if (detail.getBeforeFreeze().compareTo(BigDecimal.ZERO) < 0) {
            account.getAmount().add(detail.getChangeFreeze());
            account.getFreeze().add(detail.getChangeFreeze());
        }
        //还原账本
        Account upEntity = new Account();
        upEntity.setId(account.getId());
        upEntity.setFreeze(account.getFreeze());
        upEntity.setAmount(account.getAmount());
        accountMgDbService.save(upEntity);

        //更新账户明细
        AccountDetail upAccount = new AccountDetail();
        upAccount.setId(detail.getId());
        upAccount.setStatus(DetailStatusEnum.Rollback.getValue());
    }

    @Override
    public RPCResult<BigDecimal> queryRate(Integer sourceType, Integer targetType) {
        RPCResult<BigDecimal> result = new RPCResult<>();
        try {
            BigDecimal value = configService.findRate(sourceType, targetType);
            result.setData(value);
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            logger.error("AccountRPCServiceImpl.queryRate", e);
            result.setCode("queryRate.error");
            result.setMessage("查询汇率失败");
            return result;
        }
    }

    @Override
    public RPCResult<List<AccountDetailDto>> queryDetail(Long proxyId, String pin, Integer page, Integer size) {
        RPCResult<List<AccountDetailDto>> rpcResult = new RPCResult<>();
        try {
            List<AccountDetailDto> accountDetailDtos = accountDetailMgDbService.queryDetailList(proxyId, pin, page, size);
            if (!accountDetailDtos.isEmpty()) {
                rpcResult.setSuccess(true);
                rpcResult.setData(accountDetailDtos);
            } else {
                rpcResult.setSuccess(false);
                rpcResult.setCode("AccountRPCServiceImpl.queryDetail.null");
                rpcResult.setMessage("查询数据为空");
            }
        } catch (Exception e) {
            logger.error("AccountRPCServiceImpl.queryAccountDetail.error", e);
            rpcResult.setSuccess(false);
            rpcResult.setCode("queryDetail.error");
            rpcResult.setMessage("查询数据失败");
        }
        return rpcResult;
    }

    @Override
    public RPCResult<List<AccountDetailDto>> queryDetail(AccountDetailDto dto) {
        RPCResult<List<AccountDetailDto>> rpcResult = new RPCResult<>();
        try {

            Page<AccountDetailDto> detailDtoPage = accountDetailMgDbService.queryDetailList(dto);

            rpcResult=new RPCResult<>(detailDtoPage);
            rpcResult.setSuccess(true);
        } catch (Exception e) {
            logger.error("AccountRPCServiceImpl.queryAccountDetail.error", e);
            rpcResult.setSuccess(false);
            rpcResult.setCode("queryDetail.error");
            rpcResult.setMessage("查询数据失败");
        }
        return rpcResult;
    }

    @Override
    public RPCResult<BigDecimal> findTotal(Long proxyId, String pin, Integer targetType) {
        RPCResult<BigDecimal> result = new RPCResult<>();
        try {
            BigDecimal total = BigDecimal.ZERO;
            List<AccountDto> resultlist = getAccountDtos(pin, proxyId);
            for (AccountDto item : resultlist) {
                if (targetType.intValue() != item.getTokenType()) {
                    BigDecimal value = configService.findRate(item.getTokenType(), targetType);
                    total = total.add(item.getAmount().multiply(value));
                } else {
                    total = total.add(item.getAmount());
                }
            }
            result.setSuccess(true);
            result.setData(total);
            return result;
        } catch (Exception e) {
            logger.error("AccountRPCServiceImpl.findTotal.error", e);
            result.setSuccess(false);
            result.setCode("queryDetail.error");
            result.setMessage("查询数据失败");
        }
        return result;
    }


    @Override
    public RPCResult<List<AccountDto>> freezeAll(Long proxyId, String pin, Integer tokenType, Integer testStatus) {
        RPCResult<List<AccountDto>> result = new RPCResult<>();
        try {
            List<Account> accounts = accountMgDbService.freezeAll(proxyId, pin, tokenType, testStatus);
            List<AccountDto> resultList = new ArrayList<>();
            for (Account item : accounts) {
                AccountDto dto = new AccountDto();
                BeanCoper.copyProperties(dto, item);
                resultList.add(dto);
            }
            result.setSuccess(true);
            result.setData(resultList);
            return result;
        } catch (Exception e) {
            logger.error("查询账户失败", e);
            result.setSuccess(false);
        }
        result.setMessage("查询账户失败");
        result.setCode("account.queryAccount.error");
        return result;
    }

    @Override
    public RPCResult<Map<String,Object>> userChargeAvrge(AccountDetailDto accountDetailDto) {
        RPCResult<Map<String,Object>> rpcResult = null;
        try {
            rpcResult = new RPCResult<>();
            Map<String,Object> map = accountDetailMgDbService.avargCharge(accountDetailDto);
            rpcResult.setData(map);
            rpcResult.setSuccess(true);
            return rpcResult;
        }catch (Exception e){
            logger.error("查询用户平均充值量失败", e);
            rpcResult.setSuccess(false);
            rpcResult.setMessage("查询用户平均充值量失败");
            rpcResult.setCode("account.userChargeAvrge.error");
        }
        return rpcResult;
    }

    @Override
    public RPCResult<BigDecimal> queryChargeUsersByDay(AccountDetailDto accountDetailDto) {
        RPCResult<BigDecimal> rpcResult = null;
        try {
            rpcResult = new RPCResult<>();
            BigDecimal bigDecimal = accountDetailMgDbService.queryChargeUsers(accountDetailDto);
            rpcResult.setData(bigDecimal);
            rpcResult.setSuccess(true);
            return rpcResult;
        }catch (Exception e){
            logger.error("查询用户充值人数失败", e);
            rpcResult.setSuccess(false);
            rpcResult.setMessage("查询用户充值人数失败");
            rpcResult.setCode("account.queryChargeUsersByDay.error");
        }
        return rpcResult;
    }

    @Override
    public RPCResult<BigDecimal> queryChargeAmountByDay(AccountDetailDto accountDetailDto) {
        RPCResult<BigDecimal> rpcResult = null;
        try {
            rpcResult = new RPCResult<>();
            BigDecimal bigDecimal = accountDetailMgDbService.queryChargeAmount(accountDetailDto);
            rpcResult.setData(bigDecimal);
            rpcResult.setSuccess(true);
            return rpcResult;
        }catch (Exception e){
            logger.error("查询充值总额失败", e);
            rpcResult.setSuccess(false);
            rpcResult.setMessage("查询充值总额失败");
            rpcResult.setCode("account.queryChargeAmountByDay.error");
        }
        return rpcResult;
    }

    @Override
    public RPCResult<BigDecimal> queryProxyprofileByDay(AccountDetailDto accountDetailDto) {
        RPCResult<BigDecimal> rpcResult = null;
        try {
            rpcResult = new RPCResult<>();
            BigDecimal bigDecimal = accountDetailMgDbService.queryProxyProfile(accountDetailDto);
            rpcResult.setData(bigDecimal);
            rpcResult.setSuccess(true);
            return rpcResult;
        }catch (Exception e){
            logger.error("查询代理商盈亏失败", e);
            rpcResult.setSuccess(false);
            rpcResult.setMessage("查询代理商盈亏失败");
            rpcResult.setCode("account.queryProxyprofileByDay.error");
        }
        return rpcResult;
    }

    @Override
    public RPCResult<BigDecimal> queryChargeNewUsersByDay(AccountDetailDto accountDetailDto) {
        RPCResult<BigDecimal> rpcResult = null;
        try {
            rpcResult = new RPCResult<>();
            BigDecimal bigDecimal = accountDetailMgDbService.queryChargeNewUsers(accountDetailDto);
            rpcResult.setData(bigDecimal);
            rpcResult.setSuccess(true);
            return rpcResult;
        }catch (Exception e){
            logger.error("查询新增付费人数失败", e);
            rpcResult.setSuccess(false);
            rpcResult.setMessage("查询新增付费人数失败");
            rpcResult.setCode("account.queryChargeNewUsersByDay.error");
        }
        return rpcResult;
    }

    @Override
    public RPCResult<AccountDto> findAccount(Long proxyId, String pin, Integer tokenType, Integer testStatus) {
        RPCResult<AccountDto> result = null;
        try {
            Account query = new Account();
            query.setPin(pin);
            query.setProxyId(proxyId);
            query.setTokenType(tokenType);
            query.setTest(testStatus);
            query = accountMgDbService.findByOne(query);
            if (query == null) {
                query = new Account();
                query.setTest(testStatus);
                query.setAmount(BigDecimal.ZERO);
                query.setTokenType(tokenType);
                query.setFreeze(BigDecimal.ZERO);
                query.setPin(pin);
                query.setProxyId(proxyId);
                query.setStatus(YesOrNoEnum.YES.getValue());
            }
            AccountDto dto = BeanCoper.copyProperties(AccountDto.class, query);
            result = new RPCResult<>(dto);
            return result;
        } catch (Exception e) {
            result = new RPCResult<>();
            result.setMessage("查询账户失败");
            result.setCode("account.findAccount.error");
        }
        return result;
    }

    @Override
    public RPCResult proxyDrawalSure(Long proxyId, Long userDrawalId) {
        RPCResult result = new RPCResult();
        try {
            UserDrawalLog query = new UserDrawalLog();
            query.setProxyId(proxyId);
            query.setId(userDrawalId);
            UserDrawalLog log = userDrawalLogService.findByOne(query);
            if (log == null) {
                result.setCode("drawalSure.error");
                result.setMessage("支付确认失败,数据不存在");
                return result;
            }
            if (log.getAudiStatus() == null) {
                query.setAudiStatus(YesOrNoEnum.YES.getValue());
                query.setId(log.getId());
                query.setAudiTime(new Date());
                userDrawalLogService.save(query);
                result.setSuccess(true);
                return result;
            }
            result.setCode("drawalSure.error");
            result.setMessage("支付确认失败");
            return result;
        } catch (Exception e) {
            logger.error("drawalSure.error", e);
        }
        result.setCode("drawalSure.error");
        result.setMessage("支付确认失败");
        return result;
    }

    @Override
    public RPCResult proxyDrawalCancle(Long proxyId, Long userDrawalId) {
        RPCResult result = new RPCResult();
        try {
            UserDrawalLog query = new UserDrawalLog();
            query.setProxyId(proxyId);
            query.setId(userDrawalId);
            UserDrawalLog log = userDrawalLogService.findByOne(query);
            if (log == null) {
                result.setCode("drawalCancle.error");
                result.setMessage("支付确认失败,数据不存在");
                return result;
            }
            if (log.getAudiStatus() == null) {
                query.setAudiStatus(YesOrNoEnum.NO.getValue());
                query.setId(log.getId());
                userDrawalLogService.save(query);
                result.setSuccess(true);
                return result;
            }
        } catch (Exception e) {
            logger.error("drawalSure.error", e);
        }
        result.setCode("drawalCancle.error");
        result.setMessage("支付确认失败");
        return result;
    }


    @Override
    public RPCResult<UserBankDto> queryUserBank(UserBankDto dto) {
        RPCResult<UserBankDto> result = new RPCResult<>();
        try {

            UserBank userBank = BeanCoper.copyProperties(UserBank.class,dto);
            UserBank byOne = userBankService.findByOne(userBank);
            UserBankDto userBankDto = BeanCoper.copyProperties(UserBankDto.class, byOne);
            result.setData(userBankDto);
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            logger.error("queryUserBank.error", e);
        }
        result.setCode("queryUserBank.error");
        result.setMessage("查询用户银行卡失败");
        return result;
    }


    @Override
    public RPCResult<List<UserDrawalDto>> queryUserDrawal(UserDrawalDto dto) {
        RPCResult<List<UserDrawalDto>> result = new RPCResult<>();
        try {
            UserDrawalLog query = BeanCoper.copyProperties(UserDrawalLog.class,dto);
            if(!StringUtils.isBlank(dto.getStartDrawingDate())){
                query.setStartDrawingDate(TimeUtils.getMinTime(DateUtil.StringToDate(dto.getStartDrawingDate())));
            }
            if(!StringUtils.isBlank(dto.getEndDrawingDate())){
                query.setStartDrawingDate(TimeUtils.getMaxTime(DateUtil.StringToDate(dto.getStartDrawingDate())));
            }
            if(!StringUtils.isBlank(dto.getStartAudiTime())){
                query.setStartDrawingDate(TimeUtils.getMinTime(DateUtil.StringToDate(dto.getStartAudiTime())));
            }
            if(!StringUtils.isBlank(dto.getEndAudiTime())){
                query.setStartDrawingDate(TimeUtils.getMaxTime(DateUtil.StringToDate(dto.getEndAudiTime())));
            }

            Page<UserDrawalLog> pages = userDrawalLogService.queryByPage(query, dto.getPageinfo().getPage());
            List<UserDrawalLog> list = pages.getContent();
            result.setTotalPage(pages.getTotalPages());
            result.setPageSize(dto.getPageinfo().getPage().getPageSize());
            result.setPageIndex(dto.getPageinfo().getPage().getPageNumber());
            result.setTotalCount((int) pages.getTotalElements());
            List<UserDrawalDto> listResult = new ArrayList<>();
            for (UserDrawalLog item : list) {
                UserDrawalDto userDrawalDto = new UserDrawalDto();
                BeanCoper.copyProperties(userDrawalDto, item);
                listResult.add(userDrawalDto);
            }
            result.setData(listResult);
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            logger.error("queryUserDrawal.error", e);
        }
        result.setCode("queryUserDrawal.error");
        result.setMessage("查询支付信息失败");
        return result;
    }

    @Override
    public RPCResult verfiyPass(Long proxyId, String pin, String pass) {
        RPCResult result = new RPCResult();
        try {
            userDrawalPassService.verfiyPass(proxyId, pin, pass);
            result.setSuccess(true);
            return result;
        } catch (BizException biz) {
            result.setCode(biz.getCode());
            result.setMessage(biz.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("verfiyPass.error", e);
        }
        result.setCode("verfiyPass.error");
        result.setMessage("验证保险柜密码失败");
        return result;
    }

    @Override
    public RPCResult<WithdrawCfgDTO> viewWithdrawCfg(WithdrawCfgDTO dto) {
        RPCResult<WithdrawCfgDTO> rpcResult = null;
        try {
            rpcResult = new RPCResult<>();
            WithdrawCfgInfo entity = BeanCoper.copyProperties(WithdrawCfgInfo.class,dto);
            WithdrawCfgInfo byOne = withdrawCfgInfoService.findByOne(entity);
            WithdrawCfgDTO data = BeanCoper.copyProperties(WithdrawCfgDTO.class,byOne);
            rpcResult.setSuccess(true);
            rpcResult.setData(data);
            return rpcResult;
        }catch (Exception e){
            logger.error("查看提现配置失败", e);
            rpcResult.setSuccess(false);
            rpcResult.setMessage("查看提现配置失败");
            rpcResult.setCode("account.viewWithdrawCfg.error");
        }
        return rpcResult;
    }

    @Override
    public RPCResult saveWithdrawCfg(WithdrawCfgDTO dto) {
        RPCResult rpcResult = null;
        try {
            rpcResult = new RPCResult<>();
            WithdrawCfgInfo entity = BeanCoper.copyProperties(WithdrawCfgInfo.class,dto);
            withdrawCfgInfoService.save(entity);
            rpcResult.setSuccess(true);
            return rpcResult;
        }catch (Exception e){
            logger.error("保存提现配置失败", e);
            rpcResult.setSuccess(false);
            rpcResult.setMessage("保存提现配置失败");
            rpcResult.setCode("account.saveWithdrawCfg.error");
        }
        return rpcResult;
    }
}
