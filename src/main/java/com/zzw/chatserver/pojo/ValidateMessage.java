package com.zzw.chatserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "validatemessages")
public class ValidateMessage {
    @Id
    private ObjectId id;
    private String roomId;
    private ObjectId senderId;
    private String senderName;
    private String senderNickname; // 发送者昵称
    private String senderAvatar;  // 发送者头像
    private ObjectId receiverId;  // 接收者ID
    private String time; // 消息发送时间
    private String additionMessage; // 附加消息
    private Integer status; // 0/1/2，未处理/同意/不同意
    private Integer validateType; // 0/1, 好友/群聊
    private ObjectId groupId; //群id
}
