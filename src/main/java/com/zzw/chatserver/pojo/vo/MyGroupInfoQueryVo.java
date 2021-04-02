package com.zzw.chatserver.pojo.vo;

import com.zzw.chatserver.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyGroupInfoQueryVo {
    private String id;
    private String groupId;
    private String userId;
    private String username;
    private Integer manager;
    private Integer holder;
    private String card;
    private Date time;
    private List<User> userList;
}
