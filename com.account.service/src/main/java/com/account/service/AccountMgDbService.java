package com.account.service;

import com.account.domain.Account;
import com.account.rpc.dto.InvertBizDto;
import com.common.mongo.MongoService;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

public interface AccountMgDbService extends MongoService<Account> {

    /**
     * 转账
     * @param dto
     */

    public BigDecimal newBiz(InvertBizDto dto);


}
