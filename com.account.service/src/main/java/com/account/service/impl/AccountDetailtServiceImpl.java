package com.account.service.impl;

import com.account.dao.AccountDao;
import com.account.dao.AccountDetailtDao;
import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.rpc.dto.BizTokenEnum;
import com.account.rpc.dto.BizTypeEnum;
import com.account.domain.module.ChargeTypeEnum;
import com.account.rpc.dto.AccountDetailDto;
import com.account.domain.module.TokenTypeEnum;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.account.service.ConfigService;
import com.account.service.utils.TimeUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
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
import java.util.*;


/**
 * @desc 账本流水账
 */
@Service
public class AccountDetailtServiceImpl extends AbstractMongoService<AccountDetail> implements AccountDetailtService {

    @Resource
    private AccountDetailtDao accountDetailtDao;

    @Resource
    private AccountService accountService;

    @Resource
    private ConfigService configService;

    @Reference
    private UserRPCService userRPCService;

    @Override
    protected Class getEntityClass() {
        return AccountDetail.class;
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

//        BigDecimal rate = configService.findRate(sourceType, targetType);
//        BigDecimal total = sourceAmount.multiply(rate);

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
        upTargetAccount.setAmount(targetAccount.getAmount().add(sourceAmount));
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
        List<AccountDetail> accountDetails = query(query);
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
        AccountDetail query = BeanCoper.copyProperties(AccountDetail.class,dto);
        if(StringUtils.isBlank(dto.getQueryStartTime())){
            query.setStartCreateTime(null);
        }else{
            query.setStartCreateTime(TimeUtils.getMinTime(DateUtil.StringToDate(dto.getQueryStartTime())));
        }
        if(StringUtils.isBlank(dto.getQueryEndTime())){
            query.setEndCreateTime(null);
        }else {
            query.setEndCreateTime(TimeUtils.getMaxTime(DateUtil.StringToDate(dto.getQueryEndTime())));
        }
        query.setOrderColumn("id");
        Page<AccountDetail> accountDetails = queryByPage(query, dto.getPageinfo().getPage());
//        List<AccountDetail> list = getBaseDao().query(query);
//        Integer total = getBaseDao().queryCount(query);
//        System.out.println(System.currentTimeMillis());
//        int totalPage = total % size >0 ? total/size+1: total/size;
        if (accountDetails.hasContent()) {
            for (AccountDetail detail : accountDetails.getContent()) {
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
        Page<AccountDetailDto> detailDtoPage = new PageImpl<>(accountDetailDtos,dto.getPageinfo().getPage(),accountDetails.getTotalElements());
        return detailDtoPage;
    }

    @Override
    public Map<String,Object> avargCharge(AccountDetailDto dto) {
        Map<String,Object> map = new HashMap<>();
        UserDTO userDTO = new UserDTO();
        if(dto.getProxyId()!=null){
            userDTO.setProxyId(dto.getProxyId());
        }
//        RPCResult<List<UserDTO>> rpcResult = userRPCService.query(userDTO);
//        List<String> pins = new ArrayList<>();
//        if(rpcResult.getSuccess()){
//            for(UserDTO dd : rpcResult.getData()){
//                pins.add(dd.getPin());
//            }
//        }

        Account account = new Account();
        account.setProxyId(dto.getProxyId());
        account.setIsRobot(YesOrNoEnum.NO.getValue());
        Long aDouble =accountService.queryCount(account);
        map.put("all",BigDecimal.valueOf(aDouble));


        AccountDetail accountDetail = BeanCoper.copyProperties(AccountDetail.class,dto);
        accountDetail.setIsRobot(YesOrNoEnum.NO.getValue());
        accountDetail.setBizToken(BizTokenEnum.recharge.getValue());
        Double amount = accountDetailtDao.querySum(accountDetail);
        BigDecimal all = BigDecimal.valueOf(amount);
        RPCResult<Long> longRPCResult = userRPCService.queryUsersCount(userDTO);
        BigDecimal count = BigDecimal.valueOf(longRPCResult.getData());
        map.put("avargCharge",all.divide(count,2, BigDecimal.ROUND_HALF_UP));
        return map;
    }

    @Override
    public BigDecimal queryChargeUsers(AccountDetailDto dto) {
        AccountDetail accountDetail = BeanCoper.copyProperties(AccountDetail.class,dto);
        accountDetail.setIsRobot(YesOrNoEnum.NO.getValue());
        if(dto.getBizToken()==null){
            accountDetail.setBizToken(BizTokenEnum.recharge.getValue());
        }
        if(dto.getDayStatus()==null){
            accountDetail.setDayStatus(1);
        }
        Integer amount = accountDetailtDao.queryCount(accountDetail);
        return BigDecimal.valueOf(amount);
    }

    @Override
    public BigDecimal queryChargeAmount(AccountDetailDto dto) {
        AccountDetail accountDetail = BeanCoper.copyProperties(AccountDetail.class,dto);
        accountDetail.setIsRobot(YesOrNoEnum.NO.getValue());
        if(dto.getBizToken()==null){
            accountDetail.setBizToken(BizTokenEnum.recharge.getValue());
        }
        if(dto.getDayStatus()==null){
            accountDetail.setDayStatus(1);
        }
        Double aDouble = accountDetailtDao.querySum(accountDetail);
        return BigDecimal.valueOf(aDouble);
    }

    @Override
    public BigDecimal queryChargeNewUsers(AccountDetailDto dto) {
        AccountDetail accountDetail = BeanCoper.copyProperties(AccountDetail.class,dto);
        accountDetail.setIsRobot(YesOrNoEnum.NO.getValue());
        if(dto.getBizToken()==null){
            accountDetail.setBizToken(BizTokenEnum.recharge.getValue());
        }
        if(dto.getDayStatus()==null){
            accountDetail.setDayStatus(1);
        }
        Integer integer = accountDetailtDao.queryNewCount(accountDetail);
        return BigDecimal.valueOf(integer);
    }

    @Override
    public BigDecimal queryProxyProfile(AccountDetailDto dto) {
        AccountDetail accountDetail = BeanCoper.copyProperties(AccountDetail.class,dto);
        accountDetail.setIsRobot(YesOrNoEnum.NO.getValue());
        if(dto.getDayStatus()==null){
            accountDetail.setDayStatus(2);
        }
        Double aDouble = accountDetailtDao.querySum(accountDetail);
        return BigDecimal.valueOf(aDouble);
    }


}
