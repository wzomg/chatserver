package com.zzw.chatserver.pojo;

import com.zzw.chatserver.annon.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//账号池
@Data
@NoArgsConstructor
@Document("accountpool")
public class AccountPool {
    @AutoIncKey
    @Id
    private Long code = 10000000L; //用户或群聊标识（code字段在数据库中还是会以_id的名字存在）
    private Integer type; //1：用户；2：群聊
    private Integer status;  //1：已使用；0：未使用
}
