package com.account.service.rpc;

import com.account.domain.Account;
import com.account.domain.module.TokenTypeEnum;
import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.AccountDto;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.common.exception.BizException;
import com.common.util.BeanCoper;
import com.common.util.RPCResult;
import com.common.util.model.YesOrNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@org.apache.dubbo.config.annotation.Service
@Slf4j
public class AccountRPCServiceImpl implements AccountRPCService {


    @Resource
    private AccountService accountService;

    @Resource
    private AccountDetailtService accountDetailtService;


    @Override
    public RPCResult<List<AccountDto>> queryAccount(String pin, Long proxyId) {
        RPCResult<List<AccountDto>> result = new RPCResult<>();
        try {
            List<AccountDto> resultlist = getAccountDtos(pin, proxyId);
            result.setSuccess(true);
            result.setData(resultlist);
            return result;
        } catch (Exception e) {
            log.error("查询账户失败", e);
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
            result.setData(accountService.newBiz(dto));
            result.setSuccess(true);
            return result;
        } catch (BizException e) {
            result.setCode(e.getCode());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            log.error("执行业务失败", e);
        }
        result.setCode("account.invertBiz.error");
        result.setMessage("执行业务失败");
        result.setSuccess(false);
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
            log.error("AccountRPCServiceImpl.changeTo.error", e);
            result.setSuccess(false);
            result.setCode("changeTo.error");
            result.setMessage("转账失败");
            return result;
        }
    }


    @Override
    public RPCResult<AccountDto> findAccount(Long proxyId, String pin, Integer tokenType) {
        RPCResult<AccountDto> result = null;
        try {
            Account query = new Account();
            query.setPin(pin);
            query.setProxyId(proxyId);
            query.setTokenType(tokenType);
            query = accountService.findByOne(query);
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
            log.error("查询账户失败",e);
            result = new RPCResult<>();
            result.setMessage("查询账户失败");
            result.setCode("account.findAccount.error");
        }
        return result;
    }
}
