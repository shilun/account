package com.account.service.impl;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.module.ChargeTypeEnum;
import com.account.domain.module.TokenTypeEnum;
import com.account.rpc.dto.AccountDetailDto;
import com.account.rpc.dto.BizTokenEnum;
import com.account.rpc.dto.BizTypeEnum;
import com.account.service.AccountDetailMgDbService;
import com.account.service.AccountMgDbService;
import com.account.service.utils.TimeUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.util.*;
import com.common.util.model.YesOrNoEnum;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.passport.rpc.UserRPCService;
import com.passport.rpc.dto.UserDTO;
import org.apache.log4j.Logger;
import org.bson.conversions.Bson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * mongDB 账本记录
 */
@Service
public class AccountDetailMgDbServiceImpl extends AbstractMongoService<AccountDetail> implements AccountDetailMgDbService {

    private Logger logger = Logger.getLogger(AccountDetailMgDbServiceImpl.class);

    @Resource
    private AccountMgDbService accountMgDbService;

    @Reference
    private UserRPCService userRPCService;

    @Override
    public void changeTo(Long proxyId, String pin, Integer sourceType, BigDecimal sourceAmount, Integer targetType) {
        Account query = new Account();
        query.setPin(pin);
        query.setProxyId(proxyId);
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
        accountMgDbService.save(upSourceAccount);
        Account upTargetAccount = null;
        if (targetAccount.getId() == null) {
            upTargetAccount = targetAccount;
        } else {
            upTargetAccount = new Account();
            upTargetAccount.setId(targetAccount.getId());
        }
        upTargetAccount.setAmount(targetAccount.getAmount().add(sourceAmount));
        accountMgDbService.save(upTargetAccount);
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
                AccountDetailDto accountDetailDto = BeanCoper.copyProperties(AccountDetailDto.class, detail);
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
    public Map<String, Object> avargCharge(AccountDetailDto dto) {
        Map<String,Object> map = new HashMap<>();
        UserDTO userDTO = new UserDTO();
        if(dto.getProxyId()!=null){
            userDTO.setProxyId(dto.getProxyId());
        }

//        Account account = new Account();
//        account.setProxyId(dto.getProxyId());
//        account.setIsRobot(YesOrNoEnum.NO.getValue());
        //代理商下的金币总额开始
        List<Bson> querySum = new ArrayList<>();
        BasicDBObject match = new BasicDBObject("proxyId", dto.getProxyId());
        match.put("isRobot",YesOrNoEnum.NO.getValue());
        BasicDBObject queryMatch= new BasicDBObject("$match", match);
        querySum.add(queryMatch);
        BasicDBObject groupObject = new BasicDBObject("_id",dto.getProxyId());
        groupObject.put("amount", new BasicDBObject("$sum","$amount" ));
        BasicDBObject  queryGroup=new BasicDBObject("$group",groupObject);
        querySum.add(queryGroup);
        MongoCollection collection = this.template.getCollection("account");
        AggregateIterable<Map> aggregate = collection.aggregate(querySum, Map.class);
        MongoCursor<Map> iterator = aggregate.iterator();
        while (iterator.hasNext()){
            Map next = iterator.next();
            map.put("all",next.get("amount").toString());
        }
        //代理商下的金币总额结束
//        AccountDetail accountDetail = BeanCoper.copyProperties(AccountDetail.class,dto);
//        accountDetail.setIsRobot(YesOrNoEnum.NO.getValue());
//        accountDetail.setBizToken(BizTokenEnum.recharge.getValue());
        //代理商下平均充值量开始
        List<Bson> queryCharge = new ArrayList<>();
        BasicDBObject matchCharge = new BasicDBObject("proxyId", dto.getProxyId());
        matchCharge.put("isRobot",YesOrNoEnum.NO.getValue());
        matchCharge.put("bizToken",BizTokenEnum.recharge.getValue());
        BasicDBObject queryMatchCharge= new BasicDBObject("$match", matchCharge);
        queryCharge.add(queryMatchCharge);
        BasicDBObject groupCharge = new BasicDBObject("_id",dto.getProxyId());
        groupCharge.put("changeAmount", new BasicDBObject("$sum","$changeAmount" ));
        BasicDBObject  queryGroupCharge=new BasicDBObject("$group",groupCharge);
        queryCharge.add(queryGroupCharge);
        MongoCollection collectionCharge = this.template.getCollection("accountDetail");
        AggregateIterable<Map> aggregateIterable = collectionCharge.aggregate(queryCharge, Map.class);
        MongoCursor<Map> detailMongoCursor = aggregateIterable.iterator();
        BigDecimal charge = BigDecimal.ZERO;
        while (detailMongoCursor.hasNext()){
            Map next = detailMongoCursor.next();
            charge = BigDecimal.valueOf(Double.parseDouble(next.get("changeAmount").toString()));
        }
        //代理商下平均充结束
        RPCResult<Long> longRPCResult = userRPCService.queryUsersCount(userDTO);
        BigDecimal count = BigDecimal.valueOf(longRPCResult.getData());
        map.put("avargCharge",charge.divide(count,2, BigDecimal.ROUND_HALF_UP));
        return map;
    }

    @Override
    public BigDecimal queryChargeUsers(AccountDetailDto dto) {
        if(dto.getBizToken()==null){
            dto.setBizToken(BizTokenEnum.recharge.getValue());
        }
        if(dto.getDayStatus()==null){
            dto.setDayStatus(1);
        }
        List<BasicDBObject> querySum = new ArrayList<>();
        BasicDBObject match = new BasicDBObject("proxyId", dto.getProxyId());
        match.put("isRobot",YesOrNoEnum.NO.getValue());
        match.put("bizToken",dto.getBizToken());
        if(dto.getBizType()!=null){
            match.put("bizType",dto.getBizType());
        }
        if(dto.getDayStatus().intValue()!=1){
            Date startDate = TimeUtils.getDayStatusTime(dto.getDayStatus());
            Date endDate = TimeUtils.getDayStatusMaxTime(dto.getDayStatus());
            match.put("createTime",new BasicDBObject("$gte",startDate).put("$lt",endDate));
        }
        BasicDBObject queryMatch= new BasicDBObject("$match", match);
        //第一次group
        querySum.add(queryMatch);
        BasicDBObject groupByPin = new BasicDBObject("_id","$pin");
        BasicDBObject  queryGroup1=new BasicDBObject("$group",groupByPin);
        querySum.add(queryGroup1);
        //第二次group
        BasicDBObject groupByNull = new BasicDBObject("_id","null");
        groupByNull.put("count", new BasicDBObject("$sum",1 ));
        BasicDBObject  queryGroup2=new BasicDBObject("$group",groupByNull);
        querySum.add(queryGroup2);

        MongoCollection collection = this.template.getCollection("accountDetail");
        AggregateIterable<Map> aggregate = collection.aggregate(querySum, Map.class);
        MongoCursor<Map> iterator = aggregate.iterator();
        BigDecimal all = BigDecimal.ZERO;
        while (iterator.hasNext()){
            Map next = iterator.next();
            all = BigDecimal.valueOf(Double.parseDouble(next.get("count").toString()));
        }
        return all;
    }

    @Override
    public BigDecimal queryChargeAmount(AccountDetailDto dto) {
        if(dto.getBizToken()==null){
            dto.setBizToken(BizTokenEnum.recharge.getValue());
        }
        if(dto.getDayStatus()==null){
            dto.setDayStatus(1);
        }
        List<BasicDBObject> querySum = new ArrayList<>();
        BasicDBObject match = new BasicDBObject("proxyId", dto.getProxyId());
        match.put("isRobot",YesOrNoEnum.NO.getValue());
        match.put("bizToken",dto.getBizToken());
        if(dto.getPin()!=null){
            match.put("pin",dto.getPin());
        }
        if(dto.getUserCode()!=null){
            match.put("userCode",dto.getUserCode());
        }
        if(dto.getBizType()!=null){
            match.put("bizType",dto.getBizType());
        }
        BasicDBObject queryMatch= new BasicDBObject("$match", match);
        //第一次group
        querySum.add(queryMatch);
        BasicDBObject groupByPin = new BasicDBObject("_id","$pin");
        groupByPin.put("changeAmount", new BasicDBObject("$sum","$changeAmount" ));
        BasicDBObject  queryGroup1=new BasicDBObject("$group",groupByPin);
        querySum.add(queryGroup1);
        //第二次group
        BasicDBObject groupByNull = new BasicDBObject("_id","null");
        groupByNull.put("count", new BasicDBObject("$sum","$changeAmount" ));
        BasicDBObject  queryGroup2=new BasicDBObject("$group",groupByNull);
        querySum.add(queryGroup2);

        MongoCollection collection = this.template.getCollection("accountDetail");
        AggregateIterable<Map> aggregate = collection.aggregate(querySum, Map.class);
        MongoCursor<Map> iterator = aggregate.iterator();
        BigDecimal all = BigDecimal.ZERO;
        while (iterator.hasNext()){
            Map next = iterator.next();
            all = BigDecimal.valueOf(Double.parseDouble(next.get("count").toString()));
        }
        return all;
    }

    @Override
    public BigDecimal queryChargeNewUsers(AccountDetailDto dto) {
        if(dto.getBizToken()==null){
            dto.setBizToken(BizTokenEnum.recharge.getValue());
        }
        if(dto.getDayStatus()==null){
            dto.setDayStatus(1);
        }
        List<BasicDBObject> querySum = new ArrayList<>();
        BasicDBObject match = new BasicDBObject("proxyId", dto.getProxyId());
        match.put("isRobot",YesOrNoEnum.NO.getValue());
        match.put("bizToken",dto.getBizToken());
        if(dto.getBizToken()!=null){
            match.put("bizToken",dto.getBizToken());
        }
        if(dto.getBizType()!=null){
            match.put("bizType",dto.getBizType());
        }
        BasicDBObject queryMatch= new BasicDBObject("$match", match);
        //第一次group
        querySum.add(queryMatch);
        BasicDBObject groupByPin = new BasicDBObject("_id","$pin");
        BasicDBObject  queryGroup1=new BasicDBObject("$group",groupByPin);
        querySum.add(queryGroup1);
        //第二次group
        BasicDBObject groupByNull = new BasicDBObject("_id","null");
        groupByNull.put("count", new BasicDBObject("$sum",1 ));
        BasicDBObject  queryGroup2=new BasicDBObject("$group",groupByNull);
        querySum.add(queryGroup2);

        MongoCollection collection = this.template.getCollection("accountDetail");
        AggregateIterable<Map> aggregate = collection.aggregate(querySum, Map.class);
        MongoCursor<Map> iterator = aggregate.iterator();
        BigDecimal all = BigDecimal.ZERO;
        while (iterator.hasNext()){
            Map next = iterator.next();
            all = BigDecimal.valueOf(Double.parseDouble(next.get("count").toString()));
        }

        if(dto.getDayStatus().intValue()!=1){
            List<BasicDBObject> queryByDayStatus = new ArrayList<>();
            Date date = TimeUtils.getDayStatusTime(dto.getDayStatus());
            match.put("createTime",new BasicDBObject("$lt",date));
            BasicDBObject matchByDayStatus= new BasicDBObject("$match", match);
            queryByDayStatus.add(matchByDayStatus);
            queryByDayStatus.add(groupByPin);
            queryByDayStatus.add(groupByNull);

            AggregateIterable<Map> aggregate1 = collection.aggregate(queryByDayStatus, Map.class);
            MongoCursor<Map> iterator1 = aggregate1.iterator();
            BigDecimal byDay = BigDecimal.ZERO;
            if(iterator1.hasNext()){
                Map next = iterator1.next();
                byDay = BigDecimal.valueOf(Double.parseDouble(next.get("count").toString()));
            }
            all = all.subtract(byDay);
        }
        return all;
    }

    @Override
    public BigDecimal queryProxyProfile(AccountDetailDto dto) {
        if(dto.getDayStatus()==null){
            dto.setDayStatus(2);
        }
        List<BasicDBObject> querySum = new ArrayList<>();
        BasicDBObject match = new BasicDBObject("proxyId", dto.getProxyId());
        match.put("isRobot",YesOrNoEnum.NO.getValue());
        if(dto.getBizToken()!=null){
            match.put("bizToken",dto.getBizToken());
        }
        if(dto.getBizType()!=null){
            match.put("bizType",dto.getBizType());
        }
        if(dto.getDayStatus()!=1){
            Date startDate = TimeUtils.getDayStatusTime(dto.getDayStatus());
            Date endDate = TimeUtils.getDayStatusMaxTime(dto.getDayStatus());
            match.put("createTime",new BasicDBObject("$gte",startDate).put("$lt",endDate));
        }
        BasicDBObject queryMatch= new BasicDBObject("$match", match);
        querySum.add(queryMatch);
        //        第一次group
//        BasicDBObject groupByPin = new BasicDBObject("_id","$pin");
//        BasicDBObject  queryGroup1=new BasicDBObject("$group",groupByPin);
//        querySum.add(queryGroup1);
        //第二次group
        BasicDBObject groupByNull = new BasicDBObject("_id","null");
        groupByNull.put("count", new BasicDBObject("$sum","$changeAmount" ));
        BasicDBObject  queryGroup2=new BasicDBObject("$group",groupByNull);
        querySum.add(queryGroup2);

        MongoCollection collection = this.template.getCollection("accountDetail");
        AggregateIterable<Map> aggregate = collection.aggregate(querySum, Map.class);
        MongoCursor<Map> iterator = aggregate.iterator();
        BigDecimal all = BigDecimal.ZERO;
        while (iterator.hasNext()){
            Map next = iterator.next();
            all = BigDecimal.valueOf(Double.parseDouble(next.get("count").toString()));
        }
        return all;
    }

    @Override
    protected Class getEntityClass() {
        return AccountDetail.class;
    }
}
