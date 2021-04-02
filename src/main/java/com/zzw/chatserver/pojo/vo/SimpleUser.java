package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUser {
    private String uid;
    private String photo;
    private String signature;
    private String nickname;
    private Long onlineTime;
    private Integer level = 0; //默认等级为0
    private Date lastLoginTime;
    private String username;
}
