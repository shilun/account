package com.account.web.controller.user;

import com.account.domain.UserDrawalLog;
import com.account.rpc.AccountRPCService;
import com.account.rpc.dto.AccountDetailDto;
import com.account.rpc.dto.AccountDto;
import com.account.service.UserDrawalLogService;
import com.account.service.UserDrawalPassService;
import com.account.service.utils.RPCBeanService;
import com.account.web.AbstractClientController;
import com.account.web.controller.dto.*;
import com.common.exception.BizException;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.passport.rpc.dto.ProxyDto;
import com.passport.rpc.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(method = {RequestMethod.POST})
@Api("提款")
public class UserDrawalController extends AbstractClientController {

    @Resource
    private UserDrawalLogService userDrawalLogService;

    private static Map<String, ProxyDto> proxyMap = new HashMap<>();
    @Resource
    private UserDrawalPassService userDrawalPassService;

    @Resource
    private RPCBeanService rpcBeanService;

    @Resource
    private AccountRPCService accountRPCService;

    @ApiOperation("查询新增付费")
    @RequestMapping("account/newCharge")
    public Map<String, Object> newCharge(@RequestBody AccountDetailDto detailDto) {
        return buildMessage(() -> {
            return accountRPCService.queryChargeNewUsersByDay(detailDto);
        });
    }

    /**
     * 查询现金
     *
     * @return
     */
    @ApiOperation("查询充值总额")
    @RequestMapping("account/charge")
    public Map<String, Object> accountCharge(@RequestBody AccountDetailDto detailDto) {
        return buildMessage(() -> {
            return accountRPCService.queryChargeUsersByDay(detailDto);
        });
    }

    /**
     * 查询现金
     *
     * @return
     */
    @ApiOperation("查询保险箱金币")
    @RequestMapping("user/ver")
    public Map<String, Object> ver(Long proxyId,String pin,String pass ) {
        return buildMessage(() -> {
            return accountRPCService.verfiyPass(proxyId,pin,pass);
        });
    }
    /**
     * 查询现金
     *
     * @return
     */
    @ApiOperation("查询保险箱金币")
    @RequestMapping("user/cash")
    public Map<String, Object> cash() {
        return buildMessage(() -> {
            UserDrawalLog query = new UserDrawalLog();
            UserDTO userDTO = getUserDto();
            query.setProxyId(userDTO.getProxyId());
            query.setPin(userDTO.getPin());
            RPCResult<List<AccountDto>> listRPCResult = accountRPCService.queryAccountWithRate(userDTO.getPin(), userDTO.getProxyId());
            if (listRPCResult.getSuccess()) {
                return listRPCResult.getData();
            }
            throw new BizException(listRPCResult.getCode(), listRPCResult.getMessage());
        });
    }

    /**
     * 用户提款
     *
     * @return
     */
    @ApiOperation("用户提款")
    @RequestMapping("user/drawal")
    public Map<String, Object> drawal(@RequestBody UserDrawalDto dto) {
        return buildMessage(() -> {
            UserDrawalLog query = new UserDrawalLog();
            UserDTO userDTO = getUserDto();
            query.setProxyId(userDTO.getProxyId());
            query.setPin(userDTO.getPin());
            userDrawalLogService.drawal(userDTO.getProxyId(), userDTO.getPin(), dto.getAmount(), dto.getMsg());
            return null;
        });
    }

    /**
     * 用户提款
     *
     * @return
     */
    @ApiOperation("用户提款生成短信验证码")
    @RequestMapping("user/drawalBuildMsg")
    public Map<String, Object> drawalBuildMsg() {
        return buildMessage(() -> {
            UserDTO userDTO = getUserDto();
            userDrawalLogService.drawalBuildMsg(userDTO.getProxyId(), userDTO.getPin(), userDTO.getPhone());
            return null;
        });
    }


    /**
     * 提款密码修改
     *
     * @return
     */
    @ApiOperation("修改提款密码")
    @RequestMapping("user/drawal/passchange")
    public Map<String, Object> passchange(@RequestBody PassChangeDto dto) {
        return buildMessage(() -> {
            UserDrawalLog query = new UserDrawalLog();
            UserDTO userDTO = getUserDto();
            query.setProxyId(userDTO.getProxyId());
            query.setPin(userDTO.getPin());
            userDrawalPassService.upUserPass(userDTO.getProxyId(), userDTO.getPin(), dto.getOldPass(), dto.getNewPass());
            return null;
        });
    }

    /**
     * 提款密码忘记生成短信验证码
     *
     * @return
     */
    @ApiOperation(value = "忘记密码生成手机验证码")
    @RequestMapping("user/drawal/forgetPassBuildMsg")
    public Map<String, Object> forgetPassBuildMsg(@RequestBody MobileDto dto) {
        return buildMessage(() -> {
            userDrawalPassService.forgetPassBuildMsg(getProxy().getId(), dto.getPhoneNo());
            return null;
        });
    }

    /**
     * 提款密码忘记生成短信验证码
     *
     * @return
     */
    @ApiOperation("忘记密码生成手机验证码-验证")
    @RequestMapping("user/drawal/forgetPassVerfiyMsg")
    public Map<String, Object> forgetPassVerfiyMsg(@RequestBody ForgetPassDto dto) {
        return buildMessage(() -> {
            userDrawalPassService.forgetPassVerfiyMsg(getProxy().getId(), dto.getPhoneNo(), dto.getMsg(), dto.getPass());
            return null;
        });
    }


    /**
     * 用户提款查询
     *
     * @return
     */
    @RequestMapping("user/drawal/query")
    @ApiOperation("查询提款记录")
    public Map<String, Object> query(@RequestBody UserDragwalQueryDto dto) {
        return buildMessage(() -> {
            UserDrawalLog query = new UserDrawalLog();
            UserDTO userDTO = getUserDto();
            query.setProxyId(userDTO.getProxyId());
            query.setPin(userDTO.getPin());
            return userDrawalLogService.queryByPage(query, dto.getPageinfo().getPage());
        });
    }

    /**
     * 获取代理商信息
     *
     * @return
     */
    protected ProxyDto getProxy() {
        String domain = StringUtils.getDomain(getRequest().getRequestURL().toString());
        if (proxyMap.get(domain) != null) {
            return proxyMap.get(domain);
        }
        RPCResult<ProxyDto> result = rpcBeanService.getProxyRpcService().findByDomain(domain);
        if (result.getSuccess()) {
            ProxyDto dto = result.getData();
            proxyMap.put(domain, dto);
            return dto;
        }
        throw new BizException("server.domain.error", "服务器域名错误");
    }
}
