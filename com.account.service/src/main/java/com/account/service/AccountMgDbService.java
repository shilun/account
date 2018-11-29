package com.account.service;

import com.account.domain.Account;
import com.account.rpc.dto.InvertBizDto;
import com.common.mongo.MongoService;
import net.sf.json.JSONObject;

import java.util.List;

public interface AccountMgDbService extends MongoService<Account> {

    /**
     * 转账
     * @param dto
     */

    public void newBiz(InvertBizDto dto);


    /**
     * 冻结所有币
     * @param proxyId
     * @param pin
     * @param tokenType
     */
    List<Account> freezeAll(Long proxyId, String pin, Integer tokenType, Integer testStatus);

    void sendMqMsg(JSONObject data);
}
