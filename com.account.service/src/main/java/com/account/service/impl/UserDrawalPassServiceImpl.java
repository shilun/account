package com.account.service.impl;

import com.account.domain.UserDrawalPass;
import com.account.service.UserDrawalPassService;
import com.account.service.utils.RPCBeanService;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.security.MD5;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.passport.rpc.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserDrawalPassServiceImpl extends AbstractMongoService<UserDrawalPass> implements UserDrawalPassService {

    @Value("${app.pay.drawal.pass}")
    private String passKey;

    private static String BUILD_MSG_KEY = "pay.forget.pass.msg.{0}.{1}.{2}";

    @Resource
    private RPCBeanService rpcBeanService;

    @Override
    protected Class getEntityClass() {
        return UserDrawalPass.class;
    }


    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void upUserPass(Long proxyId, String pin, String oldPass, String newPass) {
        if (proxyId == null) {
            throw new BizException("upUserPass.error", "代理商不能为空");
        }
        if (StringUtils.isBlank(pin)) {
            throw new BizException("upUserPass.error", "pin不能为空");
        }
        if (StringUtils.isBlank(oldPass)) {
            throw new BizException("upUserPass.error", "旧密码不能为空");
        }
        if (StringUtils.isBlank(newPass)) {
            throw new BizException("upUserPass.error", "新密码不能为空");
        }
        UserDrawalPass query = new UserDrawalPass();
        query.setPin(pin);
        query.setProxyId(proxyId);
        UserDrawalPass entity = findByOne(query);
        oldPass = MD5.MD5Str(oldPass, passKey);
        newPass = MD5.MD5Str(newPass, passKey);
        if (entity == null) {
            String s = MD5.MD5Str("888888", passKey);

            if (!StringUtils.equals(oldPass, s)) {
                throw new BizException("upUserPass.error", "旧密码失败");
            }

            query.setPass(newPass);
            save(query);
            return;
        } else {
            if (entity.getPass().equals(oldPass)) {
                entity.setPass(newPass);
                save(entity);
                return;
            }
            throw new BizException("upUserPass.error", "旧密码失败");
        }


    }

    @Override
    public void verfiyPass(Long proxyId, String pin, String pass) {
        if (proxyId == null) {
            throw new BizException("verfiyPass.error", "代理商不能为空");
        }
        if (StringUtils.isBlank(pin)) {
            throw new BizException("verfiyPass.error", "pin不能为空");
        }
        if (StringUtils.isBlank(pass)) {
            throw new BizException("verfiyPass.error", "密码不能为空");
        }
        UserDrawalPass query = new UserDrawalPass();
        query.setPin(pin);
        query.setProxyId(proxyId);
        UserDrawalPass entity = findByOne(query);
        String s = MD5.MD5Str(pass, passKey);
        if (entity == null) {
            if (!StringUtils.equals(MD5.MD5Str("888888", passKey), s)) {
                throw new BizException("verfiyPass.error", "提款密码错误");
            }
            return;
        }
        if (!StringUtils.equals(entity.getPass(), s)) {
            throw new BizException("verfiyPass.error", "提款密码错误");
        }
    }

    @Override
    public void forgetPassBuildMsg(Long proxyId, String mobile) {
        String code = randomSixCode();
        String key = MessageFormat.format(BUILD_MSG_KEY, mobile, code, proxyId);
        redisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);
        UserDTO dto = new UserDTO();
        dto.setProxyId(proxyId);
        dto.setPhone(mobile);
        RPCResult<UserDTO> userResult = rpcBeanService.getUserRPCService().findByMobile(proxyId, mobile);
        if (!userResult.getSuccess()) {
            throw new BizException(userResult.getCode(), userResult.getMessage());
        }
        RPCResult<Boolean> result = rpcBeanService.getSmsInfoRPCService().buildSMSCode(mobile, "您好!您的验证码:" + code + ",有效时间3分钟，请及时验证!", "pay");
        if (!result.getSuccess()) {
            throw new BizException(result.getCode(), result.getMessage());
        }
    }

    /**
     * 生成6位随机验证码
     *
     * @return
     */
    public static String randomSixCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }

    @Override
    public void forgetPassVerfiyMsg(Long proxyId, String mobile, String msg, String pass) {
        String key = MessageFormat.format(BUILD_MSG_KEY, mobile, msg, proxyId);
        Object o = redisTemplate.opsForValue().get(key);
        if (o != null) {
            if (!msg.equals(o)) {
                throw new BizException("forgetPassVerfiyMsg.error", "验证失败");
            }
        }
        RPCResult<UserDTO> userResult = rpcBeanService.getUserRPCService().findByMobile(proxyId, mobile);
        if (!userResult.getSuccess()) {
            throw new BizException(userResult.getCode(), userResult.getMessage());
        }
        UserDrawalPass query = new UserDrawalPass();
        query.setPin(userResult.getData().getPin());
        query.setProxyId(proxyId);
        UserDrawalPass entity = findByOne(query);
        pass = MD5.MD5Str(pass, passKey);
        if (entity == null) {
            query.setPass(pass);
        } else {
            query.setId(entity.getId());
            query.setPass(pass);
        }
        save(query);
    }
}
