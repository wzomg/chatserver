package com.zzw.chatserver.dao;

import com.zzw.chatserver.pojo.AccountPool;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountPoolDao extends MongoRepository<AccountPool, String> {

}
