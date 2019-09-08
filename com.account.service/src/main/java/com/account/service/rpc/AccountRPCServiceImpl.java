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
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
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
@org.apache.dubbo.config.annotation.Service
public class AccountRPCServiceImpl implements AccountRPCService {

    private Logger logger = LoggerFactory.getLogger(AccountRPCServiceImpl.class);


    @Resource
    private AccountMgDbService accountMgDbService;

    @Resource
    private AccountDetailtService accountDetailMgDbService;
    
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

    public RPCResult<BigDecimal> invertBiz(InvertBizDto dto) {
        RPCResult<BigDecimal> result = new RPCResult<>();
        try {
            result.setData(accountMgDbService.newBiz(dto));
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
        if (detail.getChangeAmount().compareTo(BigDecimal.ZERO) < 0) {
            account.getAmount().add(detail.getChangeAmount());
        }
        //还原账本
        Account upEntity = new Account();
        upEntity.setId(account.getId());
        upEntity.setAmount(account.getAmount());
        accountMgDbService.save(upEntity);

        //更新账户明细
        AccountDetail upAccount = new AccountDetail();
        upAccount.setId(detail.getId());
        upAccount.setStatus(DetailStatusEnum.Rollback.getValue());
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
    public RPCResult<AccountDto> findAccount(Long proxyId, String pin, Integer tokenType, Integer testStatus) {
        RPCResult<AccountDto> result = null;
        try {
            Account query = new Account();
            query.setPin(pin);
            query.setProxyId(proxyId);
            query.setTokenType(tokenType);
            query = accountMgDbService.findByOne(query);
            if (query == null) {
                query = new Account();
                query.setAmount(BigDecimal.ZERO);
                query.setTokenType(tokenType);
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
//
//    @Override
//    public RPCResult proxyDrawalSure(Long proxyId, Long userDrawalId) {
//        RPCResult result = new RPCResult();
//        try {
//            UserDrawalLog query = new UserDrawalLog();
//            query.setProxyId(proxyId);
//            query.setId(userDrawalId);
//            UserDrawalLog log = userDrawalLogService.findByOne(query);
//            if (log == null) {
//                result.setCode("drawalSure.error");
//                result.setMessage("支付确认失败,数据不存在");
//                return result;
//            }
//            if (log.getAudiStatus() == null) {
//                query.setAudiStatus(YesOrNoEnum.YES.getValue());
//                query.setId(log.getId());
//                query.setAudiTime(new Date());
//                userDrawalLogService.save(query);
//                result.setSuccess(true);
//                return result;
//            }
//            result.setCode("drawalSure.error");
//            result.setMessage("支付确认失败");
//            return result;
//        } catch (Exception e) {
//            logger.error("drawalSure.error", e);
//        }
//        result.setCode("drawalSure.error");
//        result.setMessage("支付确认失败");
//        return result;
//    }
//
//    @Override
//    public RPCResult proxyDrawalCancle(Long proxyId, Long userDrawalId) {
//        RPCResult result = new RPCResult();
//        try {
//            UserDrawalLog query = new UserDrawalLog();
//            query.setProxyId(proxyId);
//            query.setId(userDrawalId);
//            UserDrawalLog log = userDrawalLogService.findByOne(query);
//            if (log == null) {
//                result.setCode("drawalCancle.error");
//                result.setMessage("支付确认失败,数据不存在");
//                return result;
//            }
//            if (log.getAudiStatus() == null) {
//                query.setAudiStatus(YesOrNoEnum.NO.getValue());
//                query.setId(log.getId());
//                userDrawalLogService.save(query);
//                result.setSuccess(true);
//                return result;
//            }
//        } catch (Exception e) {
//            logger.error("drawalSure.error", e);
//        }
//        result.setCode("drawalCancle.error");
//        result.setMessage("支付确认失败");
//        return result;
//    }

//
//    @Override
//    public RPCResult<List<UserDrawalDto>> queryUserDrawal(UserDrawalDto dto) {
//        RPCResult<List<UserDrawalDto>> result = new RPCResult<>();
//        try {
//            UserDrawalLog query = BeanCoper.copyProperties(UserDrawalLog.class,dto);
//            if(!StringUtils.isBlank(dto.getStartDrawingDate())){
//                query.setStartDrawingDate(TimeUtils.getMinTime(DateUtil.StringToDate(dto.getStartDrawingDate())));
//            }
//            if(!StringUtils.isBlank(dto.getEndDrawingDate())){
//                query.setStartDrawingDate(TimeUtils.getMaxTime(DateUtil.StringToDate(dto.getStartDrawingDate())));
//            }
//            if(!StringUtils.isBlank(dto.getStartAudiTime())){
//                query.setStartDrawingDate(TimeUtils.getMinTime(DateUtil.StringToDate(dto.getStartAudiTime())));
//            }
//            if(!StringUtils.isBlank(dto.getEndAudiTime())){
//                query.setStartDrawingDate(TimeUtils.getMaxTime(DateUtil.StringToDate(dto.getEndAudiTime())));
//            }
//
//            Page<UserDrawalLog> pages = userDrawalLogService.queryByPage(query, dto.getPageinfo().getPage());
//            List<UserDrawalLog> list = pages.getContent();
//            result.setTotalPage(pages.getTotalPages());
//            result.setPageSize(dto.getPageinfo().getPage().getPageSize());
//            result.setPageIndex(dto.getPageinfo().getPage().getPageNumber());
//            result.setTotalCount((int) pages.getTotalElements());
//            List<UserDrawalDto> listResult = new ArrayList<>();
//            for (UserDrawalLog item : list) {
//                UserDrawalDto userDrawalDto = new UserDrawalDto();
//                BeanCoper.copyProperties(userDrawalDto, item);
//                listResult.add(userDrawalDto);
//            }
//            result.setData(listResult);
//            result.setSuccess(true);
//            return result;
//        } catch (Exception e) {
//            logger.error("queryUserDrawal.error", e);
//        }
//        result.setCode("queryUserDrawal.error");
//        result.setMessage("查询支付信息失败");
//        return result;
//    }
//
//    @Override
//    public RPCResult verfiyPass(Long proxyId, String pin, String pass) {
//        RPCResult result = new RPCResult();
//        try {
//            userDrawalPassService.verfiyPass(proxyId, pin, pass);
//            result.setSuccess(true);
//            return result;
//        } catch (BizException biz) {
//            result.setCode(biz.getCode());
//            result.setMessage(biz.getMessage());
//            return result;
//        } catch (Exception e) {
//            logger.error("verfiyPass.error", e);
//        }
//        result.setCode("verfiyPass.error");
//        result.setMessage("验证保险柜密码失败");
//        return result;
//    }
//
//    @Override
//    public RPCResult<WithdrawCfgDTO> viewWithdrawCfg(WithdrawCfgDTO dto) {
//        RPCResult<WithdrawCfgDTO> rpcResult = null;
//        try {
//            rpcResult = new RPCResult<>();
//            WithdrawCfgInfo entity = BeanCoper.copyProperties(WithdrawCfgInfo.class,dto);
//            WithdrawCfgInfo byOne = withdrawCfgInfoService.findByOne(entity);
//            WithdrawCfgDTO data = BeanCoper.copyProperties(WithdrawCfgDTO.class,byOne);
//            rpcResult.setSuccess(true);
//            rpcResult.setData(data);
//            return rpcResult;
//        }catch (Exception e){
//            logger.error("查看提现配置失败", e);
//            rpcResult.setSuccess(false);
//            rpcResult.setMessage("查看提现配置失败");
//            rpcResult.setCode("account.viewWithdrawCfg.error");
//        }
//        return rpcResult;
//    }
//
//    @Override
//    public RPCResult saveWithdrawCfg(WithdrawCfgDTO dto) {
//        RPCResult rpcResult = null;
//        try {
//            rpcResult = new RPCResult<>();
//            WithdrawCfgInfo entity = BeanCoper.copyProperties(WithdrawCfgInfo.class,dto);
//            withdrawCfgInfoService.save(entity);
//            rpcResult.setSuccess(true);
//            return rpcResult;
//        }catch (Exception e){
//            logger.error("保存提现配置失败", e);
//            rpcResult.setSuccess(false);
//            rpcResult.setMessage("保存提现配置失败");
//            rpcResult.setCode("account.saveWithdrawCfg.error");
//        }
//        return rpcResult;
//    }
}
