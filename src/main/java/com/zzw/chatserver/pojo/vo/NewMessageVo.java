package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMessageVo {
    private String roomId;
    private String senderId;// 发送者Id
    private String senderName;// 发送者登录名
    private String senderNickname;// 发送者昵称
    private String senderAvatar; // 发送者头像
    private Date time;// 消息发送时间
    private String fileRawName; //文件的原始名字
    private String message;// 消息内容
    private String messageType;// 消息的类型：emoji/text/img/file/sys
    private List<String> isReadUser; // 判断已经读取的用户，在发送消息时默认发送方已读取
    private String conversationType;
}
