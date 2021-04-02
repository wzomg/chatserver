package com.zzw.chatserver.service;


import com.zzw.chatserver.dao.AccountPoolDao;
import com.zzw.chatserver.pojo.AccountPool;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccountPoolService {
    @Resource
    private AccountPoolDao accountPoolDao;

    public void saveAccount(AccountPool accountPool) {
        accountPoolDao.save(accountPool);
    }
}
