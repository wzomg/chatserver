package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleRecentConversationResultVo {
    private String id;
    private String createDate;//用字符串显示
    private SimpleUser userM;
    private SimpleUser userY;
}
