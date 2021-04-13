package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackResultVo {
    private String id; //主键
    private String userId; //反馈人id
    private String username; //反馈人账号名
    private String feedBackContent; //反馈内容
    private Date createTime; //反馈时间
}
