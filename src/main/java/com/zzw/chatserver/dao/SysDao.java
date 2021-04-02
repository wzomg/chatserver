package com.zzw.chatserver.dao;

import com.zzw.chatserver.pojo.SystemUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SysDao extends MongoRepository<SystemUser, ObjectId> {
}
