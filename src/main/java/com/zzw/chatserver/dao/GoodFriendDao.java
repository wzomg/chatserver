package com.zzw.chatserver.dao;

import com.zzw.chatserver.pojo.GoodFriend;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface GoodFriendDao extends MongoRepository<GoodFriend, ObjectId> {
}

