package com.zzw.chatserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document("groupusers")
public class GroupUser {
    @Id
    private ObjectId guid;
    private ObjectId groupId;
    private ObjectId userId; //成员id
    private String username; //成员账号名
    private Integer manager = 0; // 是否是管理员，默认0，不是，1是（可以设置一下这个需求）
    private Integer holder = 0;  // 是否是群主，默认0，不是，1是
    private String card = "";  // 群名片
    private Date time = new Date(); // 设置默认时间
}
