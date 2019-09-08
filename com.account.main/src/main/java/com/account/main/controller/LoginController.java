package com.account.main.controller;

import com.account.main.AbstractClientController;
import com.account.main.controller.dto.LoginDto;
import com.account.main.controller.dto.PasswordChangeDto;
import com.common.exception.BizException;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.common.web.IExecute;
import com.passport.rpc.AdminRPCService;
import com.passport.rpc.dto.UserDTO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@RestController
@RequestMapping(value = "/login", method = {RequestMethod.POST})
public class LoginController extends AbstractClientController {

    @Reference
    protected AdminRPCService adminRPCService;
    @RequestMapping("in")
    public Map<String, Object> login(@RequestBody LoginDto dto, HttpServletResponse response) {
        return buildMessage(new IExecute() {
            @Override
            public Object getData() {
                RPCResult<UserDTO> login = adminRPCService.login(dto.getAccount(), dto.getCode());
                if (!login.getSuccess()) {
                    throw new BizException("loginError", "登录失败，登录账户或密码错误");
                }
                putCookie("token", login.getData().getToken(), response);
                return login.getData();
            }
        });
    }


    @RequestMapping("out")
    public Map<String,Object> loginOut() {
        return buildMessage(new IExecute() {
            @Override
            public Object getData() {
                return adminRPCService.loginOut(getToken());
            }
        });
    }

    @RequestMapping("check")
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

    @RequestMapping("changePass")
    public Map<String, Object> changePass(@RequestBody PasswordChangeDto dto) {
        return buildMessage(new IExecute() {
            @Override
            public Object getData() {
                return adminRPCService.changePass(getPin(), dto.getOldPassword(), dto.getNewPassword());
            }
        });
    }

}