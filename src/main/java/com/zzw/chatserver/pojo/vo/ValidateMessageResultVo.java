package com.zzw.chatserver.pojo.vo;

import com.zzw.chatserver.pojo.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateMessageResultVo {
    private String id;
    private String roomId;
    private String senderId;
    private String senderName;
    private String senderNickname; // 发送者昵称
    private String senderAvatar;  // 发送者头像
    private String receiverId;  // 接收者ID
    private String time; // 消息发送时间
    private String additionMessage; // 附加消息
    private Integer status; // 0/1/2，未处理/同意/不同意
    private Integer validateType; // 0/1, 好友/群聊
    private String groupId; //群聊id
    private List<Group> groupList; //可能包含群聊验证消息
}
