package com.zzw.chatserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("superusers")
public class SuperUser {
    @Id
    private ObjectId sid;
    private String account;
    private String password;
    private Integer role; //角色分类：超级管理员0，具有增删改查权限；普通管理员1，只有有查的权限
    private String nickname = "wzomg-admin";
    private String avatar = "img/admin-avatar.gif";
}
