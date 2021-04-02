package com.zzw.chatserver.dao;

import com.zzw.chatserver.pojo.SingleMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SingleMessageDao extends MongoRepository<SingleMessage, ObjectId> {

}
