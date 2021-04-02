package com.zzw.chatserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("systemusers") //系统用户，用于验证消息的发送
public class SystemUser {
    @Id
    private ObjectId id;
    private String code;
    private String nickname;
    private Integer status;
}
