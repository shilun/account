package com.account.service.impl;

import com.account.dao.AccountDao;
import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.WithdrawCfgInfo;
import com.account.rpc.dto.BizTokenEnum;
import com.account.rpc.dto.BizTypeEnum;
import com.account.domain.module.DetailStatusEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.account.service.WithdrawCfgInfoService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.common.exception.ApplicationException;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.redis.DistributedLock;
import com.common.redis.DistributedLockUtil;
import com.common.util.*;
import com.common.util.model.YesOrNoEnum;
import com.passport.rpc.UserRPCService;
import com.passport.rpc.dto.UserDTO;
import com.version.MqKey;
import com.version.mq.service.api.IMqService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;


/**
 * @desc 账户信息 account
 */
@Service
public class AccountServiceImpl extends AbstractMongoService<Account> implements AccountService {
    private Logger logger = Logger.getLogger(AccountServiceImpl.class);
    private AccountDao accountDao;

    @Resource
    private DistributedLockUtil distributedLockUtil;
    private String user_login_key = "account.newBiz.pin.{0}";
    @Resource
    private AccountDetailtService accountDetailtService;
    @Reference
    private UserRPCService userRPCService;
    @Resource
    private IMqService iMqService;
    @Resource
    private WithdrawCfgInfoService withdrawCfgInfoService;
    @Value("${daishan.rocketmq.topic.prefix}")
    private String prefix;

    @Override
    protected Class getEntityClass() {
        return Account.class;
    }

    @Transactional
    public void newBiz(InvertBizDto dto) {
        if (dto.getAmount() == null && dto.getFreeze() == null) {
            throw new BizException("dto.error", "数据验证失败,acmount or freeze null");
        }
        if (dto.getProxyId() == null) {
            throw new BizException("dto.error.proxyId", "数据验证失败,proxyId null");
        }
        if (dto.getTokenType() == null) {
            throw new BizException("dto.error.tokenType", "数据验证失败,tokenType null");
        }
        if (StringUtils.isBlank(dto.getBizId())) {
            throw new BizException("dto.error.BizId", "数据验证失败, bizId null");
        }
        if (dto.getBizType() == null) {
            throw new BizException("dto.error.bizType", "数据验证失败 bizType null");
        }
        if (dto.getBizToken() == null) {
            throw new BizException("dto.error.bizToken", "数据验证失败 bizToken null");
        }
        if (StringUtils.isBlank(dto.getPin())) {
            throw new BizException("dto.error.pin", "数据验证失败 pin null");
        }
        //默认为正试账户
        if (dto.getTest() == null) {
            dto.setTest(YesOrNoEnum.NO.getValue());
        }
        if (dto.getTokenType() == null) {
            throw new BizException("dto.error.tokenType", "数据验证失败");
        }
        if (dto.getFreeze() == null) {
            dto.setFreeze(BigDecimal.ZERO);
        }
        if (dto.getAmount() == null) {
            dto.setAmount(BigDecimal.ZERO);
        }
        //提现判断
        if(dto.getBizToken()==BizTokenEnum.drawing.getValue().intValue()){
            WithdrawCfgInfo withdrawCfgInfo = new WithdrawCfgInfo();
            withdrawCfgInfo.setProxyId(dto.getProxyId());
            withdrawCfgInfo = withdrawCfgInfoService.findByOne(withdrawCfgInfo);
            if(withdrawCfgInfo.getStatus()==YesOrNoEnum.NO.getValue().intValue()){
                throw new BizException("dto.error.status", "不允许提现");
            }
            if(withdrawCfgInfo.getMaxMoney().compareTo(dto.getAmount()) < 0){
                throw new BizException("dto.error.maxMoney", "超出最大提现金额");
            }
            if(withdrawCfgInfo.getMinMoney().compareTo(dto.getAmount()) >0){
                throw new BizException("dto.error.minMoney", "小于最小提现金额");
            }
        }
        String lock_key = MessageFormat.format(user_login_key, dto.getPin());
        DistributedLock distributedLock = distributedLockUtil.getDistributedLock(lock_key, 30 * 1000);
        boolean acquire = distributedLock.acquire();
        if (!acquire) {
            return;
        }
        try {
            AccountDetail findDetail = new AccountDetail();

            findDetail.setBizType(dto.getBizType());
            findDetail.setBizToken(dto.getBizToken());
            RPCResult<UserDTO> byPin = userRPCService.findByPin(dto.getProxyId(), dto.getPin());
            int isRobot = 2;
            if(byPin.getSuccess() && byPin.getData()!=null){
                isRobot = YesOrNoEnum.NO.getValue();
            }else {
                isRobot = YesOrNoEnum.YES.getValue();
            }
            findDetail.setIsRobot(isRobot);
            findDetail.setProxyId(dto.getProxyId());
            findDetail.setBizId(dto.getBizId());
            findDetail.setChargeType(dto.getChargeType());
            findDetail.setTest(dto.getTest());
            findDetail = accountDetailtService.findByOne(findDetail);
            if (findDetail != null) {
                return;
            }
            Account query = new Account();
            query.setProxyId(dto.getProxyId());
            query.setPin(dto.getPin());
            query.setTest(dto.getTest());
            BizTypeEnum bizTypeEnum = GlosseryEnumUtils.getItem(BizTypeEnum.class, dto.getBizType());
            BizTokenEnum bizTokenEnum = BizTokenEnum.consume;
            if(dto.getBizToken() != null){
                bizTokenEnum = GlosseryEnumUtils.getItem(BizTokenEnum.class, dto.getBizToken());
            }
            query.setTokenType(dto.getTokenType());
            Account account = findByOne(query);
            if (account == null) {
                account = new Account();
                account.setIsRobot(isRobot);
                account.setTokenType(dto.getTokenType());
                account.setFreeze(BigDecimal.ZERO);
                account.setAmount(BigDecimal.ZERO);
                account.setTest(dto.getTest());
                account.setProxyId(dto.getProxyId());
                account.setPin(dto.getPin());
                if(isRobot==YesOrNoEnum.NO.getValue()) {
                    account.setUserCode(byPin.getData().getId());
                }
            }
            AccountDetail detail = new AccountDetail();
            detail.setIsRobot(isRobot);
            detail.setPin(dto.getPin());
            if(isRobot==YesOrNoEnum.NO.getValue().intValue()) {
                detail.setUserCode(byPin.getData().getId());
            }
            detail.setTest(dto.getTest());
            detail.setProxyId(dto.getProxyId());
            detail.setTokenType(dto.getTokenType());
            detail.setStatus(YesOrNoEnum.YES.getValue());
            detail.setBizType(bizTypeEnum.getValue());
            detail.setBizToken(bizTokenEnum.getValue());
            detail.setBizId(dto.getBizId());
            detail.setTest(dto.getTest());
            if(dto.getChargeType()!=null){
                detail.setChargeType(dto.getChargeType());
            }else {
                detail.setChargeType(0);
            }
            detail.setBeforeAmount(account.getAmount());
            detail.setBeforeFreeze(account.getFreeze());
            account.setAmount(account.getAmount().add(dto.getAmount()));
            account.setFreeze(account.getFreeze().add(dto.getFreeze()));
            detail.setChangeAmount(dto.getAmount());
            detail.setChangeFreeze(dto.getFreeze());
            detail.setAfterAmount(account.getAmount());
            detail.setAfterFreeze(account.getFreeze());
            if (account.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                if(dto.getBizToken()!=BizTokenEnum.qipaiconsume.getValue().intValue()){
                    throw new BizException("account.error", "账户余额不足");
                }
            }
            if (account.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
                if(dto.getBizToken()!=BizTokenEnum.qipaiconsume.getValue().intValue()){
                    throw new BizException("account.error", "冻结金额不足");
                }
            }
            if (account.getAmount().compareTo(account.getFreeze()) < 0) {
                if(dto.getBizToken()!=BizTokenEnum.qipaiconsume.getValue().intValue()){
                    throw new BizException("account.error", "账户金额不足");
                }
            }
            if (account.getId() == null) {
                save(account);
            } else {
                Account upEntity = new Account();
                upEntity.setId(account.getId());
                upEntity.setAmount(account.getAmount());
                upEntity.setFreeze(account.getFreeze());
                up(upEntity);
            }
            detail.setStatus(DetailStatusEnum.Normal.getValue());
            accountDetailtService.insert(detail);
            if(dto.getBizToken()==BizTokenEnum.recharge.getValue().intValue()){
                JSONObject data = new JSONObject();
                data.put("pin",dto.getPin());
                data.put("proxyId",dto.getProxyId());
                data.put("bizType",dto.getBizType());
                data.put(MqKey.COM_VERSION_MQ_KEY,"recharge");
                iMqService.pushToMq(prefix+"account",data.toString());

            }
        } catch (BizException biz) {
            throw biz;
        } catch (Exception e) {
            logger.error("newBiz.error", e);
            throw new ApplicationException("newBiz.error");
        } finally {
            distributedLock.release();
        }
    }
    /**
     * 锁定用户所有币key
     */
    private String accountFreezeAll_key = "account.freezeAll.pin.{0}";

    @Override
    public List<Account> freezeAll(Long proxyId, String pin, Integer tokenType, Integer testStatus) {
        String lock_key = MessageFormat.format(accountFreezeAll_key, pin);
        if (testStatus == null) {
            testStatus = YesOrNoEnum.NO.getValue();
        }
        DistributedLock distributedLock = null;
        try {
            boolean acquire = false;
            if (testStatus == YesOrNoEnum.NO.getValue().intValue()) {
                distributedLock = distributedLockUtil.getDistributedLock(lock_key, 30 * 1000);
                acquire = distributedLock.acquire();
            }
            if (!acquire) {
                throw new BizException("freezeAll.error", "冻结失败");
            }
            Account query = new Account();
            query.setProxyId(proxyId);
            query.setPin(pin);
            query.setTest(testStatus);
            List<Account> list = query(query);
            for (Account item : list) {
                if (item.getTokenType().intValue() == tokenType.intValue()) {
                    Account upEntity = new Account();
                    upEntity.setId(item.getId());
                    upEntity.setFreeze(item.getAmount());
                    item.setFreeze(item.getAmount());
                    up(upEntity);
                }
            }
            return list;
        } catch (Exception e) {
            throw new BizException("freezeAll.error", "冻结失败");
        } finally {
            if (testStatus == YesOrNoEnum.NO.getValue().intValue()) {
                distributedLock.release();
            }

        }
    }
}
