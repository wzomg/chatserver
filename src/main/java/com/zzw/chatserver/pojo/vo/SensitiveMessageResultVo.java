package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveMessageResultVo {
    private String id; //敏感词消息编号
    private String roomId; //房间号
    private String senderId; // 发送者Id
    private String senderName; // 发送者登录名
    private String time;// 消息发送时间
    private String message;// 消息内容
    private String type; // 0/1/2, 好友/群聊/验证消息
}
