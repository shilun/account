package com.account.service.impl;

import com.account.dao.AccountDetailtDao;
import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.module.BizTypeEnum;
import com.account.domain.module.ChargeTypeEnum;
import com.account.rpc.dto.AccountDetailDto;
import com.account.domain.module.TokenTypeEnum;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.account.service.ConfigService;
import com.account.service.utils.TimeUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.common.exception.BizException;
import com.common.util.*;
import com.common.util.model.YesOrNoEnum;
import com.passport.rpc.UserRPCService;
import com.passport.rpc.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @desc 账本流水账
 */
@Service
public class AccountDetailtServiceImpl extends DefaultBaseService<AccountDetail> implements AccountDetailtService {

    @Resource
    private AccountDetailtDao accountDetailtDao;

    @Resource
    private AccountService accountService;

    @Resource
    private ConfigService configService;

    @Reference
    private UserRPCService userRPCService;

    @Override
    public AbstractBaseDao<AccountDetail> getBaseDao() {
        return accountDetailtDao;
    }

    @Transactional
    public void changeTo(Long proxyId, String pin, Integer sourceType, BigDecimal sourceAmount, Integer targetType) {
        Account query = new Account();
        query.setPin(pin);
        query.setProxyId(proxyId);
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
                e.setStatus(YesOrNoEnum.NO.getValue());
                list.add(e);
            }
        }
        Account sourceAccount = null;
        Account targetAccount = null;
        for (Account item : list) {
            if (sourceType.intValue()==item.getTokenType()) {
                sourceAccount = item;
            }
            if (targetType.intValue()==item.getTokenType()) {
                targetAccount = item;
            }
            if (sourceAccount != null && targetAccount != null) {
                break;
            }
        }

        BigDecimal rate = configService.findRate(sourceType, targetType);
        BigDecimal total = sourceAmount.multiply(rate);

        sourceAccount.setAmount(sourceAccount.getAmount().subtract(sourceAmount));

        if (sourceAccount.getAmount().compareTo(sourceAccount.getFreeze()) < 0) {
            throw new BizException("changeTo.error", "转账失败,余额不足");
        }
        Account upSourceAccount = null;
        if (sourceAccount.getId() == null) {
            upSourceAccount = sourceAccount;
        } else {
            upSourceAccount = new Account();
            upSourceAccount.setId(sourceAccount.getId());
        }
        upSourceAccount.setAmount(sourceAccount.getAmount());
        accountService.save(upSourceAccount);
        Account upTargetAccount = null;
        if (targetAccount.getId() == null) {
            upTargetAccount = targetAccount;
        } else {
            upTargetAccount = new Account();
            upTargetAccount.setId(targetAccount.getId());
        }
        upTargetAccount.setAmount(targetAccount.getAmount().add(total));
        accountService.save(upTargetAccount);
    }

    @Override
    public List<AccountDetailDto> queryDetailList(Long proxyId, String pin, Integer page, Integer size) {
        List<AccountDetailDto> accountDetailDtos = new ArrayList<>();
        AccountDetail query = new AccountDetail();
        query.setProxyId(proxyId);
        query.setPin(pin);
        query.setOrderColumn("id");
        query.setOrderTpe(2);
        List<AccountDetail> accountDetails = getBaseDao().query(query);
        if (!accountDetails.isEmpty()) {
            for (AccountDetail detail : accountDetails) {
                AccountDetailDto accountDetailDto = new AccountDetailDto();
                BeanCoper.copyProperties(accountDetailDto, detail);
                accountDetailDtos.add(accountDetailDto);
            }
        }
        return accountDetailDtos;
    }

    @Override
    public Page<AccountDetailDto> queryDetailList(AccountDetailDto dto) {
        List<AccountDetailDto> accountDetailDtos = new ArrayList<>();
        int page = dto.getPageinfo().getPage().getPageNumber();
        int size = dto.getPageinfo().getSize();
        AccountDetail query = BeanCoper.copyProperties(AccountDetail.class,dto);
        if(StringUtils.isBlank(dto.getQueryStartTime()) && StringUtils.isBlank(dto.getQueryEndTime())){
//           Date date = new Date();
            query.setStartCreateTime(null);
            query.setEndCreateTime(null);
        }else{
            query.setStartCreateTime(TimeUtils.getMinTime(DateUtil.StringToDate(dto.getQueryStartTime())));
            query.setEndCreateTime(TimeUtils.getMaxTime(DateUtil.StringToDate(dto.getQueryEndTime())));
        }
        query.setOrderColumn("id");
        query.setOrderTpe(2);
        query.setStartRow(page * size);
        query.setEndRow(size);
        List<AccountDetail> accountDetails = getBaseDao().query(query);
        Integer total = getBaseDao().queryCount(query);
//        int totalPage = total % size >0 ? total/size+1: total/size;
        if (!accountDetails.isEmpty()) {
            for (AccountDetail detail : accountDetails) {
                AccountDetailDto accountDetailDto = new AccountDetailDto();
                BeanCoper.copyProperties(accountDetailDto, detail);
                if(accountDetailDto.getBizType() != null){
                    accountDetailDto.setBizTypeName(GlosseryEnumUtils.getItem(BizTypeEnum.class,accountDetailDto.getBizType()).getName());
                }
                if(accountDetailDto.getTokenType() != null){
                    accountDetailDto.setTokenName(GlosseryEnumUtils.getItem(TokenTypeEnum.class,accountDetailDto.getTokenType()).getName());
                }
                if(accountDetailDto.getChargeType() != null){
                    accountDetailDto.setChargeTypeName(GlosseryEnumUtils.getItem(ChargeTypeEnum.class,accountDetailDto.getChargeType()).getName());
                }
                accountDetailDtos.add(accountDetailDto);
            }
        }
        Page<AccountDetailDto> detailDtoPage = new PageImpl<>(accountDetailDtos,dto.getPageinfo().getPage(),total.longValue());
        return detailDtoPage;
    }

    @Override
    public BigDecimal avargCharge(AccountDetailDto dto) {
        AccountDetail detail = BeanCoper.copyProperties(AccountDetail.class,dto);
        Double amount = accountDetailtDao.querySum(detail);
        BigDecimal all = BigDecimal.valueOf(amount);
        UserDTO userDTO = new UserDTO();
        if(dto.getProxyId()!=null){
            userDTO.setProxyId(dto.getProxyId());
        }
        RPCResult<Long> longRPCResult = userRPCService.queryUsersCount(userDTO);
        BigDecimal count = BigDecimal.valueOf(longRPCResult.getData());
        return all.divide(count);
    }


}
