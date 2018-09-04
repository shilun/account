package com.account.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.common.exception.BizException;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.common.web.AbstractController;
import com.passport.rpc.AdminRPCService;
import com.passport.rpc.dto.UserDTO;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;

/**
 * Created by shilun on 2017/5/12.
 */
public abstract class AbstractClientController extends AbstractController {

    @Reference
    protected AdminRPCService adminRPCService;
    private static final Logger LOGGER = Logger.getLogger(AbstractClientController.class);


    protected UserDTO getUser() {
        String token = getToken();
        RPCResult<UserDTO> userDTOResult = adminRPCService.verificationToken(token);
        if (userDTOResult.getSuccess()) {
            return userDTOResult.getData();
        }
        LOGGER.error("token.error:" + token);
        throw new BizException("token.error", "获取token失败");
    }

    protected String getToken() {
        String token = getRequest().getHeader("token");
        if (StringUtils.isBlank(token)) {
            Cookie tokenCookie = null;
            for (Cookie item : getRequest().getCookies()) {
                if (StringUtils.equals(item.getName(), "token")) {
                    tokenCookie = item;
                    break;
                }
            }
            token = tokenCookie.getValue();
        }
        return token;
    }

    protected String getPin() {
        String token = getToken();
        RPCResult<UserDTO> userDTOResult = adminRPCService.verificationToken(token);
        if (userDTOResult.getSuccess()) {
            return userDTOResult.getData().getPin();
        }
        LOGGER.error("token.error:" + token);
        throw new BizException("pin.error", "获取pin失败");
    }

    protected String getVer() {
        String ver = getRequest().getHeader("ver");
        if (StringUtils.isBlank(ver)) {
            throw new BizException("ver.null", "获取当前版本信息失败");
        }
        return ver;
    }
}
