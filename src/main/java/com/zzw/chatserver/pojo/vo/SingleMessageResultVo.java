package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  SingleMessageResultVo {
    private String id;
    private String roomId;
    private String senderId;
    private String senderName;
    private String senderNickname;
    private String senderAvatar;
    private Date time = new Date();
    private String fileRawName; //文件的原始名字
    private String message;
    private String messageType;
    private List<String> isReadUser = new ArrayList<>();
}
