package com.zzw.chatserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document("feedbacks")
public class FeedBack {
    @Id
    private ObjectId id; //主键
    private ObjectId userId; //反馈人id
    private String username; //反馈人账号名
    private String feedBackContent; //反馈内容
    private Date createTime = new Date(); //反馈时间
}
