package com.account.service.impl;

import com.account.domain.UserBank;
import com.account.domain.UserDrawalLog;
import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.AccountDto;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountService;
import com.account.service.UserBankService;
import com.account.service.UserDrawalLogService;
import com.account.service.utils.RPCBeanService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.common.util.model.YesOrNoEnum;
import com.passport.rpc.UserRPCService;
import com.passport.rpc.dto.UserDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class UserDrawalLogServiceImpl extends AbstractMongoService<UserDrawalLog> implements UserDrawalLogService {


    @Override
    protected Class getEntityClass() {
        return UserDrawalLog.class;
    }

    @Resource
    private RPCBeanService rpcBeanService;
    @Resource
    private AccountRPCService accountRPCService;

    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    /**
     * 短信内容
     */
    private String MSG_CONTENT = "您好!您的验证码:{0},有效时间3分钟，请及时验证!";
    /**
     * 提款
     */
    private static Integer BIZ_TYP_DRAW_MONEY = 2;
    /**
     * 人民币账户
     */
    private Integer RMB_TYPE = 1;

    @Resource
    private RedisTemplate redisTemplate;


    @Resource
    private UserBankService userBankService;
    @Reference
    private UserRPCService userRPCService;
    /**
     * 提款验证码短信
     */
    private static String USER_DRAWAL_KEY = "pay.user.drawal.{0}";

    @Override
    public void drawal(Long proxyId, String pin, BigDecimal amount, String msg) {
//        String key = MessageFormat.format(USER_DRAWAL_KEY, pin);
//        Object o = redisTemplate.opsForValue().get(key);
//        if (StringUtils.isBlank(msg) || !msg.equals(o)) {
//            throw new BizException("drawal.msg.error", "提款验证码失败");
//        }
        BigDecimal total = BigDecimal.ZERO;
        RPCResult<List<AccountDto>> listRPCResult = accountRPCService.queryAccount(pin, proxyId);
        if (listRPCResult.getSuccess()) {
            List<AccountDto> data = listRPCResult.getData();
            for (AccountDto item : data) {
                if (RMB_TYPE.intValue() == item.getTokenType()) {
                    total = total.add(item.getAmount());
                }
            }
        }
        if (amount.compareTo(total) > 1) {
            throw new BizException("user.drawal.error", "余额不足提款失败");
        }
        UserDrawalLog log = new UserDrawalLog();
        RPCResult<UserDTO> byPin = userRPCService.findByPin(proxyId, pin);
        log.setPin(pin);
        log.setAmount(amount);
        log.setProxyId(proxyId);
        log.setStatus(YesOrNoEnum.NO.getValue());
        log.setDetainStatus(YesOrNoEnum.NO.getValue());
        log.setUserCode(byPin.getData().getId());
        save(log);
        fixedThreadPool.execute(() -> {
            InvertBizDto dto = new InvertBizDto();
            dto.setBizType(BIZ_TYP_DRAW_MONEY);
            dto.setBizToken(BIZ_TYP_DRAW_MONEY);
            dto.setTokenType(RMB_TYPE);
            dto.setBizId(log.getId().toString());
            dto.setPin(pin);
            dto.setProxyId(proxyId);
            dto.setAmount(BigDecimal.ZERO.subtract(amount));
            dto.setFreeze(BigDecimal.ZERO);
            dto.setTest(YesOrNoEnum.NO.getValue());
            RPCResult result = accountRPCService.invertBiz(dto);
            if (result.getSuccess()) {
                UserDrawalLog up = new UserDrawalLog();
                up.setId(log.getId());
                up.setDetainStatus(YesOrNoEnum.YES.getValue());
                up(up);
            }
        });
    }

    @Override
    public void drawalBuildMsg(Long proxyId, String pin, String phoneNo) {
        UserBank entity = new UserBank();
        entity.setProxyId(proxyId);
        entity.setPin(pin);
        UserBank byOne = userBankService.findByOne(entity);
        if(byOne==null){
            throw new BizException("bank.blank.error", "银行卡不能为空");
        }
        String code = StringUtils.randomSixCode();
        String content = MessageFormat.format(MSG_CONTENT, code);
        RPCResult<Boolean> msgResult = rpcBeanService.getSmsInfoRPCService().buildSMSCode(phoneNo, content, "pay.drawal");
        if (!msgResult.getSuccess()) {
            throw new BizException(msgResult.getCode(), msgResult.getMessage());
        }
        content = MessageFormat.format(USER_DRAWAL_KEY, pin);
        redisTemplate.opsForValue().set(content, code, 3, TimeUnit.MINUTES);
    }
}
