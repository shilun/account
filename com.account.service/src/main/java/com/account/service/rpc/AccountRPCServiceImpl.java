package com.account.service.rpc;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.module.BizTypeEnum;
import com.account.domain.module.DetailStatusEnum;
import com.account.domain.module.TokenTypeEnum;
import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.AccountDetailDto;
import com.account.rpc.dto.AccountDto;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.account.service.ConfigService;
import com.common.exception.BizException;
import com.common.util.BeanCoper;
import com.common.util.RPCResult;
import com.common.util.model.YesOrNoEnum;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@com.alibaba.dubbo.config.annotation.Service
public class AccountRPCServiceImpl implements AccountRPCService {

    private Logger logger = Logger.getLogger(AccountRPCServiceImpl.class);

    @Resource
    private AccountService accountService;

    @Resource
    private AccountDetailtService accountDetailtService;

    @Resource
    private ConfigService configService;


    @Override
    public RPCResult<List<AccountDto>> queryAccountWithRate(String pin, Long proxyId) {
        RPCResult<List<AccountDto>> result = null;
        try {
            List<AccountDto> resultlist = getAccountDtos(pin, proxyId);
            for (AccountDto dto : resultlist) {
                if (TokenTypeEnum.RMB.getValue().intValue() == dto.getTokenType()) {
                    dto.setRate(BigDecimal.ONE);
                } else {
                    BigDecimal rate = configService.findRate(TokenTypeEnum.RMB.getValue(), dto.getTokenType());
                    dto.setRate(rate);
                }
                dto.setAmount(dto.getAmount().subtract(dto.getFreeze()));
                dto.setFreeze(BigDecimal.ZERO);
            }
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
            Page<Account> accounts = accountService.queryByPage(query, new PageRequest(pageIndex, pageSize));
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
        List<Account> list = accountService.query(query);
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

            accountService.newBiz(dto);
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
                accountService.newBiz(dto);
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
            accountDetailtService.changeTo(proxyId, pin, sourceType, sourceAmount, targetType);
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
        AccountDetail detail = accountDetailtService.findByOne(query);
        Account queryAccount = new Account();
        queryAccount.setPin(detail.getPin());
        queryAccount.setTokenType(detail.getTokenType());
        Account account = accountService.findByOne(queryAccount);
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
        accountService.up(upEntity);

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
            List<AccountDetailDto> accountDetailDtos = accountDetailtService.queryDetailList(proxyId, pin, page, size);
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
            Page<AccountDetailDto> detailDtoPage = accountDetailtService.queryDetailList(dto);
            if (detailDtoPage.hasContent()) {
                rpcResult.setSuccess(true);
                rpcResult.setData(detailDtoPage.getContent());
                rpcResult.setTotalCount(Integer.valueOf((int) detailDtoPage.getTotalElements()));
                rpcResult.setPageIndex(dto.getPageinfo().getPage().getPageNumber());
                rpcResult.setTotalPage(detailDtoPage.getTotalPages());
                rpcResult.setPageSize(detailDtoPage.getSize());
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
            List<Account> accounts = accountService.freezeAll(proxyId, pin, tokenType, testStatus);
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
    public RPCResult<AccountDto> findAccount(Long proxyId, String pin, Integer tokenType, Integer testStatus) {
        RPCResult<AccountDto> result = null;
        try {
            Account query = new Account();
            query.setPin(pin);
            query.setProxyId(proxyId);
            query.setTokenType(tokenType);
            query.setTest(testStatus);
            query = accountService.findByOne(query);
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
}
