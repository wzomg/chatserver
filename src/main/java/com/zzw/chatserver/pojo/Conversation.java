package com.zzw.chatserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    private String name;
    private String photo;
    private String id;
    private String type; // 会话类型 group/ frend
}
