package com.account.service.rpc;

import com.account.domain.Account;
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

@Service
@org.apache.dubbo.config.annotation.Service
@Slf4j
public class AccountRPCServiceImpl implements AccountRPCService {


    @Resource
    private AccountService accountService;

    @Resource
    private AccountDetailtService accountDetailtService;


    @Override
    public RPCResult<AccountDto> findAccount(String pin, Integer tokenType) {
        RPCResult<AccountDto> result = new RPCResult<>();
        try {
            AccountDto resultlist = getAccountDtos(pin, tokenType);
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


    private AccountDto getAccountDtos(String pin, Integer tokenType) {
        Account account = new Account();
        account.setPin(pin);
        account.setTokenType(tokenType);
        account = accountService.findByOne(account);
        if (account == null) {
            account = new Account();
            account.setVer(0);
            account.setPin(pin);
            account.setStatus(YesOrNoEnum.YES.getValue());
            account.setAmount(BigDecimal.ZERO);
        }
        return BeanCoper.copyProperties(AccountDto.class, account);
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

}
