package com.zzw.chatserver.dao;

import com.zzw.chatserver.pojo.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupDao extends MongoRepository<Group, ObjectId> {
}
