package com.account.service.impl;

import com.account.domain.WithdrawCfgInfo;
import com.account.service.WithdrawCfgInfoService;
import com.common.mongo.AbstractMongoService;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: CSL
 * @Date: 2018/9/11 11:40
 */
@Service
public class WithdrawCfgInfoServiceImpl extends AbstractMongoService<WithdrawCfgInfo> implements WithdrawCfgInfoService {

    private static Logger logger = Logger.getLogger(WithdrawCfgInfoServiceImpl.class);


    @Resource
    private MongoClient client;
    @Override
    protected Class getEntityClass() {
        ClientSession clientSession =null;
            try{
                clientSession = client.startSession();
                clientSession.startTransaction();

                clientSession.commitTransaction();
            }
            catch (Exception e){
                clientSession.abortTransaction();
            }
            finally {
                clientSession.close();
            }

        return WithdrawCfgInfo.class;
    }


}
