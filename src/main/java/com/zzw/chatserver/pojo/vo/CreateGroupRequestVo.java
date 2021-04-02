package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupRequestVo {
    private String title;
    private String desc;
    private String img;
    private String holderName;
    private String holderUserId;
}
