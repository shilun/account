package com.account.web.controller.user;

import com.account.domain.UserBank;
import com.account.service.UserBankService;
import com.account.web.AbstractClientController;
import com.account.web.controller.dto.UserBankDto;
import com.common.util.BeanCoper;
import com.passport.rpc.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(method = {RequestMethod.POST})
@Api("用户银行账号")
public class UserBankController extends AbstractClientController {
    @Resource
    private UserBankService userBankService;

    /**
     * 添加玩家用户银行卡
     *
     * @return
     */
    @RequestMapping("user/bank/add")
    @ApiOperation(value = "添加")
    public Map<String, Object> addUserBank(@RequestBody UserBankDto dto) {
        return buildMessage(() -> {
            UserDTO userDTO = getUserDto();
            UserBank userBank = new UserBank();
            userBank.setPin(userDTO.getPin());
            userBank.setProxyId(userDTO.getProxyId());
            userBank.setBankType(dto.getBankType());
            UserBank old = userBankService.findByOne(userBank);
            if(old==null){
                userBank.setBankName(dto.getBankName());
                userBank.setBankType(dto.getBankType());
                userBank.setCode(dto.getCode());
                userBank.setImg(dto.getImg());
                userBank.setAddress(dto.getAddress());
                userBank.setName(dto.getName());
                userBankService.save(userBank);
            }else {
                old.setName(dto.getName());
                old.setBankName(dto.getBankName());
                old.setBankType(dto.getBankType());
                old.setCode(dto.getCode());
                old.setAddress(dto.getAddress());
                old.setUserCode(userDTO.getId());
                old.setPin(userDTO.getPin());
                userBankService.save(old);
            }
            return null;
        });
    }

    /**
     * 修改玩家用户银行卡
     *
     * @return
     */
    @ApiOperation(value = "修改")
    @RequestMapping("user/bank/up")
    public Map<String, Object> up(@RequestBody UserBankDto dto) {
        return buildMessage(() -> {
            UserDTO userDTO = getUserDto();
            UserBank entity = new UserBank();
            BeanCoper.copyProperties(entity, dto);
            entity.setProxyId(userDTO.getProxyId());
            entity.setPin(userDTO.getPin());
            entity.setUserCode(userDTO.getId());
            userBankService.upUserBank(entity);
            return null;
        });

    }

    /**
     * 删除玩家银行卡
     *
     * @param content
     * @return
     */
    @RequestMapping("user/bank/del")
    @ApiOperation(value = "删除", notes = "内容为json {id:'xxxxx'}")
    public Map<String, Object> delUserBank(@RequestBody String content) {
        return buildMessage(() -> {
            UserDTO userDTO = getUserDto();
            userBankService.delUserBank(JSONObject.fromObject(content).getLong("id"), userDTO.getPin());
            return null;
        });
    }

    @RequestMapping("user/bank/query")
    public Map<String, Object> query() {
        return buildMessage(() -> {
            UserDTO userDTO = getUserDto();
            UserBank query = new UserBank();
            query.setPin(userDTO.getPin());
            return userBankService.query(query);
        });
    }

}
