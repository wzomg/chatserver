package com.zzw.chatserver.dao;

import com.zzw.chatserver.pojo.ValidateMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ValidateMessageDao extends MongoRepository<ValidateMessage, ObjectId> {
    ValidateMessage findValidateMessageByRoomIdAndStatusAndValidateType(String roomId, Integer status, Integer validateType);

}
