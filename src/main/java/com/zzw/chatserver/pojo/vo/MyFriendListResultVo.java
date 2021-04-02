package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyFriendListResultVo {
    private Date createDate;
    private String nickname;
    private String photo;
    private String signature;
    private String id;
    private Integer level; //grade
    private String roomId;
}
