package com.account.service.rpc;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.module.DetailStatusEnum;
import com.account.domain.module.TokenTypeEnum;
import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.AccountDto;
import com.account.rpc.dto.BizTypeEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.common.exception.BizException;
import com.common.util.BeanCoper;
import com.common.util.RPCResult;
import org.apache.log4j.Logger;
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

    @Override
    public RPCResult<List<AccountDto>> queryAccount(String pin, Long proxyId) {
        RPCResult<List<AccountDto>> result = new RPCResult<>();
        try {
            List<AccountDto> resultlist = getAccountDtos(pin, proxyId);
            result.setSuccess(true);
            result.setData(resultlist);
            return result;
        } catch (Exception e) {
            logger.error("查询账户失败");
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
        List<Account> emptyList = new ArrayList<>();
        for (TokenTypeEnum type : TokenTypeEnum.values()) {
            boolean moneyed = false;
            for (Account item : list) {
                if (type.name().equalsIgnoreCase(item.getTokenType())) {
                    moneyed = true;
                    break;
                }
            }
            if (!moneyed) {
                Account e = new Account();
                e.setPin(pin);
                e.setProxyId(proxyId);
                e.setAmount(BigDecimal.ZERO);
                e.setFreeze(BigDecimal.ZERO);
                e.setTokenType(type.name());
                list.add(e);
            }
        }
        List<AccountDto> resultlist = new ArrayList<>();
        for (Account item : list) {
            AccountDto dto = new AccountDto();
            BeanCoper.copyProperties(dto, item);
            resultlist.add(dto);
        }
        return resultlist;
    }

    @Override

    public RPCResult<Boolean> invertBiz(InvertBizDto dto) {
        RPCResult result = new RPCResult<>();
        try {
            newBiz(dto);
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            logger.error("执行业务失败", e);
        }
        result.setCode("account.invertBiz.error");
        result.setMessage("执行业务失败");
        result.setSuccess(false);
        return result;
    }

    @Transactional
    public void newBiz(InvertBizDto dto) {
        Account query = new Account();
        query.setProxyId(dto.getProxyId());
        query.setPin(dto.getPin());
        Account account = accountService.findByOne(query);
        if (account == null) {
            account = new Account();
            account.setTokenType(dto.getTokenType());
            account.setFreeze(BigDecimal.ZERO);
            account.setAmount(BigDecimal.ZERO);
            account.setProxyId(dto.getProxyId());
            account.setPin(dto.getPin());
        }
        AccountDetail detail = new AccountDetail();
        detail.setBeforeAmount(account.getAmount());
        detail.setBeforeFreeze(account.getFreeze());
        if (dto.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            account.setAmount(account.getAmount().add(dto.getAmount()));
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            account.getAmount().subtract(dto.getAmount());
        }
        if (dto.getFreeze().compareTo(BigDecimal.ZERO) > 0) {
            account.setFreeze(account.getFreeze().add(dto.getFreeze()));
        }
        if (dto.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
            account.setAmount(account.getAmount().subtract(dto.getFreeze()));
            account.setFreeze(account.getFreeze().subtract(dto.getFreeze()));
        }
        if (account.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException("invertBiz.account.error", "执行业务失败,余额不足");
        }
        if (account.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException("invertBiz.freeze.error", "执行业务失败,冻结账户不足");
        }
        detail.setChangeAmount(dto.getAmount());
        detail.setChangeFreeze(dto.getFreeze());

        detail.setAfterAmount(account.getAmount());
        detail.setAfterFreeze(account.getFreeze());

        if (account.getId() == null) {
            accountService.save(account);
        } else {
            Account upEntity = new Account();
            upEntity.setId(upEntity.getId());
            upEntity.setAmount(account.getAmount());
            upEntity.setFreeze(account.getFreeze());
        }
        detail.setStatus(DetailStatusEnum.Normal.getValue());
        accountDetailtService.save(detail);
    }


    @Override
    public RPCResult<Boolean> invertBizBack(BizTypeEnum bizType, Long bizId) {
        RPCResult result = new RPCResult();
        try {
            rollBackBiz(bizType, bizId);
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            logger.error("业务回滚失败", e);
        }
        result.setSuccess(false);
        result.setCode("account.invertBizBack.error");
        result.setMessage("业务回滚失败");
        return result;
    }

    @Transactional
    public void rollBackBiz(BizTypeEnum bizType, Long bizId) {
        AccountDetail query = new AccountDetail();
        query.setBizType(bizType.getValue());
        query.setBizId(bizId);
        AccountDetail detail = accountDetailtService.findByOne(query);
        Account queryAccount = new Account();
        queryAccount.setPin(detail.getPin());
        queryAccount.setTokenType(detail.getTokenName());
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
}
