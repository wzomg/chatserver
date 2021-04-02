package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchGroupResponseVo {
    private String gid; // 群标识
    private String title;// 群名称
    private String desc;
    private String img;
    private String code;
    private Integer userNum;
    private Date createDate;
    private String holderName;
    private SimpleUser holderUserInfo = new SimpleUser();
}
