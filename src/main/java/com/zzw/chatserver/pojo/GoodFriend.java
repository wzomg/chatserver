package com.zzw.chatserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("goodfriends")
public class GoodFriend {
    @Id
    private ObjectId id;
    private ObjectId userM;
    private ObjectId userY;
    private Date createDate = new Date();  // 加好友时间
}
