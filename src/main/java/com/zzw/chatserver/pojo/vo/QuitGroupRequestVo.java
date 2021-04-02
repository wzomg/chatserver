package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitGroupRequestVo {
    private Integer holder; //若是群主则需要验证当前登录用户
    private String groupId; //群id（必须）
    private String userId; //当前操作人的id
}
