package com.account.service.impl;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.WithdrawCfgInfo;
import com.account.domain.module.DetailStatusEnum;
import com.account.rpc.dto.BizTokenEnum;
import com.account.rpc.dto.BizTypeEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailMgDbService;
import com.account.service.AccountMgDbService;
import com.account.service.AccountService;
import com.account.service.WithdrawCfgInfoService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.common.exception.ApplicationException;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.redis.DistributedLock;
import com.common.redis.DistributedLockUtil;
import com.common.util.GlosseryEnumUtils;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.common.util.model.YesOrNoEnum;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.session.ClientSession;
import com.passport.rpc.UserRPCService;
import com.passport.rpc.dto.UserDTO;
import com.version.MqKey;
import com.version.mq.service.api.IMqService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * mongoDB account账户信息
 */
@Service
public class AccountMgDbServiceImpl extends AbstractMongoService<Account> implements AccountMgDbService {
    private Logger logger = Logger.getLogger(AccountMgDbServiceImpl.class);
    @Resource
    private MongoClient mongoClient;
    @Resource
    private DistributedLockUtil distributedLockUtil;
    /**
     * 执行account业务key
     */
    private String user_login_key = "account.newBiz.pin.{0}";
    /**
     * 锁定用户所有币key
     */
    private String accountFreezeAll_key = "account.freezeAll.pin.{0}";

    @Resource
    private Executor executor;

    @Resource
    @Lazy
    private AccountDetailMgDbService accountDetailMgDbService;
    @Reference
    private UserRPCService userRPCService;
    @Resource
    private IMqService iMqService;
    @Resource
    private AccountService accountService;
    @Resource
    private WithdrawCfgInfoService withdrawCfgInfoService;
    @Value("${daishan.rocketmq.topic.prefix}")
    private String prefix;
    @Value("${daishan.rocketmq.log:false}")
    private boolean logOpen;


    @Override
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
        if(dto.getBizToken()== BizTokenEnum.drawing.getValue().intValue()){
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
        AccountDetail findDetail = new AccountDetail();

        findDetail.setBizType(dto.getBizType());
        findDetail.setBizToken(dto.getBizToken());
        RPCResult<UserDTO> byPin = userRPCService.findByPin(dto.getProxyId(), dto.getPin());
        int isRobot = 2;
        Long userCode=null;
        if(byPin.getSuccess() && byPin.getData()!=null){
            isRobot = YesOrNoEnum.NO.getValue();
            userCode = byPin.getData().getId();
        }else {
            isRobot = YesOrNoEnum.YES.getValue();
        }
        findDetail.setIsRobot(isRobot);
        findDetail.setProxyId(dto.getProxyId());
        findDetail.setBizId(dto.getBizId());
        findDetail.setChargeType(dto.getChargeType());
        findDetail.setTest(dto.getTest());
        findDetail = accountDetailMgDbService.findByOne(findDetail);
        if (findDetail != null) {
            return;
        }
        String lock_key = MessageFormat.format(user_login_key, dto.getPin());
        DistributedLock distributedLock = distributedLockUtil.getDistributedLock(lock_key, 600 * 1000);
        boolean acquire = distributedLock.acquire();
        if (!acquire) {
            return;
        }
        try {
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
                    account.setUserCode(userCode);
                }
            }
            AccountDetail detail = new AccountDetail();
            detail.setIsRobot(isRobot);
            detail.setPin(dto.getPin());
            if(isRobot==YesOrNoEnum.NO.getValue().intValue()) {
                detail.setUserCode(userCode);
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
                }else {
                    logger.warn("棋牌游戏消费："+dto.getAmount()+"元，账户余额不足");
                }
            }
            if (account.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
                if(dto.getBizToken()!=BizTokenEnum.qipaiconsume.getValue().intValue()){
                    throw new BizException("account.error", "冻结金额不足");
                }else {
                    logger.warn("棋牌游戏取消冻结："+dto.getFreeze()+"元，冻结金额不足");
                }
            }
            if (account.getAmount().compareTo(account.getFreeze()) < 0) {
                if(dto.getBizToken()!=BizTokenEnum.qipaiconsume.getValue().intValue()){
                    throw new BizException("account.error", "账户金额不足");
                }else {
                    logger.warn("棋牌游戏冻结账户："+dto.getFreeze()+"元，账户金额不足");
                }
            }
            /**
             * mongoDB 事务
             */
            ClientSession clientSession = mongoClient.startSession();
            com.mongodb.client.ClientSession clientSession1 = (com.mongodb.client.ClientSession) clientSession;
            clientSession1.startTransaction(TransactionOptions.builder().readPreference(ReadPreference.primary()).build());
                try {
                    clientSession1.startTransaction();
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
                    accountDetailMgDbService.save(detail);
                    clientSession1.commitTransaction();
                } catch (Exception e) {
                    clientSession1.abortTransaction();
                    logger.error("account.error",e);
                    throw new Exception("account.error", e);
                }
            if(dto.getBizToken()==BizTokenEnum.recharge.getValue().intValue()){
                JSONObject data = new JSONObject();
                data.put("pin",dto.getPin());
                data.put("proxyId",dto.getProxyId());
                data.put("bizType",dto.getBizType());
                data.put(MqKey.COM_VERSION_MQ_KEY,"recharge");
                if (userCode != null) {
                    data.put("userCode", String.valueOf(userCode));
                }
                data.put(MqKey.COM_VERSION_MQ_KEY, "recharge");
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        sendMqMsg(data);
                    }
                });
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
            /**
             * mongoDB 事务
             */
            try (ClientSession clientSession = mongoClient.startSession()) {
                com.mongodb.client.ClientSession clientSession1 = (com.mongodb.client.ClientSession) clientSession;
                clientSession1.startTransaction(TransactionOptions.builder().readPreference(ReadPreference.primary()).build());
                try {
                    for (Account item : list) {
                        if (item.getTokenType().intValue() == tokenType.intValue()) {
                            Account upEntity = new Account();
                            upEntity.setId(item.getId());
                            upEntity.setFreeze(item.getAmount());
                            item.setFreeze(item.getAmount());
                            up(upEntity);
                        }
                    }
                    clientSession1.commitTransaction();
                } catch (Exception e) {
                    ((com.mongodb.client.ClientSession) clientSession).abortTransaction();
                    throw new BizException("freezeAll.error", "冻结失败");
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
    @Override
    public void sendMqMsg(JSONObject data){
        logger.warn("begin sent mq message...");
        iMqService.pushToMq("mq-topic-account", data.toString());
        if (logOpen) {
            logger.warn("recharge sent mq message ok"+data.toString());
        }

    }

    @Override
    protected Class getEntityClass() {
        return Account.class;
    }
}
