package com.account.main.worker;

import com.account.service.AccountDetailMgDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AccountDetailWorker {
    private Logger logger = LoggerFactory.getLogger(AccountDetailWorker.class);

    private volatile AtomicBoolean create=new AtomicBoolean(false);

    @Resource
    private AccountDetailMgDbService accountDetailMgDbService;

    /**
     * 校验accounDetail 数据是否正常
     */
    @Scheduled(cron="0 0 0 * * ?")
    public void validateAccountDetail(){

        if (!create.getAndSet(true)) {
            try {
                logger.info("===>>开始校验accountDetail数据。。。");
                accountDetailMgDbService.verfiyDeateilStatus();
            } catch (Exception e) {
                logger.error("validateAccountDetail:error", e);
            }
            create.set(false);
        }


    }
}
