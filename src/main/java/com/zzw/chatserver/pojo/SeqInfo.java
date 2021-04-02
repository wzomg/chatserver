package com.zzw.chatserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sequence")
public class SeqInfo {
    @Id
    private ObjectId id;// 主键
    private String collName;// 集合名称
    private Long seqId;// 序列值
}
