package com.account.service;

import com.account.domain.UserDrawalPass;
import com.common.mongo.MongoService;

/**
 * 用户提款密码服务
 */
public interface UserDrawalPassService extends MongoService<UserDrawalPass> {


    /**
     * 修改提款密码
     *
     * @param proxyId
     * @param pin
     * @param oldPass
     * @param newPass
     */
    void upUserPass(Long proxyId, String pin, String oldPass, String newPass);


    /**
     * 验证提款密码
     *
     * @param proxyId
     * @param pin
     * @param pass
     * @return
     */
    public void verfiyPass(Long proxyId, String pin, String pass);

    /**
     * 忘记密码生成短信
     * @param mobile
     */
    void forgetPassBuildMsg(Long proxyId, String mobile);

    /**
     * 忘记官吏码短信验证
     * @param mobile
     * @param msg
     */
    void forgetPassVerfiyMsg(Long proxyId, String mobile, String msg, String pass);
}
