package com.account.web.controller;

import com.account.web.AbstractClientController;
import com.account.web.controller.dto.LoginDto;
import com.common.exception.BizException;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.common.web.IExecute;
import com.passport.rpc.AdminRPCService;
import com.passport.rpc.dto.UserDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
@RequestMapping(value = "/login", method = {RequestMethod.POST, RequestMethod.OPTIONS})
public class LoginController extends AbstractClientController {

    @Resource
    private AdminRPCService adminRPCService;

    @RequestMapping("in")
    @ResponseBody
    public Map<String, Object> login(@RequestBody LoginDto dto, HttpServletResponse response) {
        return buildMessage(new IExecute() {
            @Override
            public Object getData() {
                RPCResult<UserDTO> login = adminRPCService.login(dto.getAccount(), dto.getCode());
                if (login == null && !login.getSuccess()) {
                    throw new BizException("loginError", "登录失败，登录账户或密码错误");
                }
                return login.getData();
            }
        });
    }


    @RequestMapping("out")
    @ResponseBody
    public RPCResult<Boolean> loginOut() {
        return buildRPCMessage(new IExecute() {
            @Override
            public Object getData() {
                return adminRPCService.loginOut(getToken());
            }
        });
    }


    @RequestMapping("check")
    @ResponseBody
    public Map<String, Object> check() {
        return buildMessage(new IExecute() {
            @Override
            public Object getData() {
                String token = getToken();
                if (StringUtils.isBlank(token)) {
                    throw new BizException("token.error", "token error");
                }
                RPCResult<UserDTO> userDTOResult = adminRPCService.verificationToken(token);
                return userDTOResult.getSuccess();
            }
        });
    }
}
