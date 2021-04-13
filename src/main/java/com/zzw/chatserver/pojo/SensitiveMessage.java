package com.zzw.chatserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("sensitivemessages")
public class SensitiveMessage {
    @Id
    private ObjectId id; //发送者编号
    private String roomId; //房间号
    private String senderId; // 发送者Id
    private String senderName; // 发送者登录名
    private String time;// 消息发送时间
    private String message;// 消息内容
    private String type; // 0/1/2, 好友/群聊/验证消息
}
