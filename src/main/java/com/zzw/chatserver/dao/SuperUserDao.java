package com.zzw.chatserver.dao;

import com.zzw.chatserver.pojo.SuperUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SuperUserDao extends MongoRepository<SuperUser, ObjectId> {

}
