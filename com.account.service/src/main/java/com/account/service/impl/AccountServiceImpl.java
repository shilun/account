package com.account.service.impl;

import com.account.dao.AccountDao;
import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.WithdrawCfgInfo;
import com.account.domain.module.BizTokenEnum;
import com.account.domain.module.BizTypeEnum;
import com.account.domain.module.DetailStatusEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.account.service.WithdrawCfgInfoService;
import com.common.exception.ApplicationException;
import com.common.exception.BizException;
import com.common.redis.DistributedLock;
import com.common.redis.DistributedLockUtil;
import com.common.util.AbstractBaseDao;
import com.common.util.DefaultBaseService;
import com.common.util.GlosseryEnumUtils;
import com.common.util.StringUtils;
import com.common.util.model.YesOrNoEnum;
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
public class AccountServiceImpl extends DefaultBaseService<Account> implements AccountService {
    private Logger logger = Logger.getLogger(AccountServiceImpl.class);
    @Resource
    private AccountDao accountDao;

    @Resource
    private DistributedLockUtil distributedLockUtil;
    private String user_login_key = "account.newBiz.pin.{0}";
    @Resource
    private AccountDetailtService accountDetailtService;
    @Resource
    private IMqService iMqService;
    @Resource
    private WithdrawCfgInfoService withdrawCfgInfoService;
    @Value("${daishan.rocketmq.topic.prefix}")
    private String prefix;

    @Override
    public AbstractBaseDao<Account> getBaseDao() {
        return accountDao;
    }

    @Transactional
    public void newBiz(InvertBizDto dto) {
        if (dto.getAmount() == null && dto.getFreeze() == null) {
            throw new BizException("dto.error", "数据验证失败");
        }
        if (dto.getProxyId() == null) {
            throw new BizException("dto.error.BizId", "数据验证失败");
        }
        if (dto.getTokenType() == null) {
            throw new BizException("dto.error.BizId", "数据验证失败");
        }
        if (StringUtils.isBlank(dto.getBizId())) {
            throw new BizException("dto.error.BizId", "数据验证失败");
        }
        if (dto.getBizType() == null) {
            throw new BizException("dto.error.BizId", "数据验证失败");
        }
        if (StringUtils.isBlank(dto.getPin())) {
            throw new BizException("dto.error.pin", "数据验证失败");
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
        if(dto.getBizToken()==BizTokenEnum.drawing.getValue()){
            WithdrawCfgInfo withdrawCfgInfo = new WithdrawCfgInfo();
            withdrawCfgInfo.setProxyId(dto.getProxyId());
            withdrawCfgInfo = withdrawCfgInfoService.findByOne(withdrawCfgInfo);
            if(withdrawCfgInfo.getStatus()==YesOrNoEnum.NO.getValue()){
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
            if(dto.getIsRobot()!=null){
                findDetail.setIsRobot(dto.getIsRobot());
            }else {
                findDetail.setIsRobot(YesOrNoEnum.NO.getValue());
            }
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
                if(dto.getIsRobot()!=null){
                    account.setIsRobot(dto.getIsRobot());
                }else {
                    account.setIsRobot(YesOrNoEnum.NO.getValue());
                }
                account.setTokenType(dto.getTokenType());
                account.setFreeze(BigDecimal.ZERO);
                account.setAmount(BigDecimal.ZERO);
                account.setTest(dto.getTest());
                account.setProxyId(dto.getProxyId());
                account.setPin(dto.getPin());
            }
            AccountDetail detail = new AccountDetail();
            if(dto.getIsRobot()!=null){
                detail.setIsRobot(dto.getIsRobot());
            }else {
                detail.setIsRobot(YesOrNoEnum.NO.getValue());
            }
            detail.setPin(dto.getPin());
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
                throw new BizException("account.error", "账户余额不足");
            }
            if (account.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException("account.error", "冻结金额不足");
            }
            if (account.getAmount().compareTo(account.getFreeze()) < 0) {
                throw new BizException("account.error", "账户金额不足");
            }
            if (account.getId() == null) {
                save(account);
            } else {
                Account upEntity = new Account();
                if(dto.getIsRobot()!=null){
                    upEntity.setIsRobot(dto.getIsRobot());
                }else {
                    upEntity.setIsRobot(YesOrNoEnum.NO.getValue());
                }
                upEntity.setId(account.getId());
                upEntity.setAmount(account.getAmount());
                upEntity.setFreeze(account.getFreeze());
                up(upEntity);
            }
            detail.setStatus(DetailStatusEnum.Normal.getValue());
            accountDetailtService.add(detail);
            if(dto.getBizType()==BizTypeEnum.qipai.getValue()){
                JSONObject data = new JSONObject();
                data.put("pin",dto.getPin());
                data.put("proxyId",dto.getProxyId());
                iMqService.pushToMq(prefix+"qipai",data.toString());
                System.out.println("mq消息");
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
            if (testStatus == YesOrNoEnum.NO.getValue()) {
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
            if (testStatus == YesOrNoEnum.NO.getValue()) {
                distributedLock.release();
            }

        }
    }
}
