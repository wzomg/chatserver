package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryMsgRequestVo {
    private String roomId;//群id
    private String type;//类型
    private String query; //搜索内容
    private Date date; //日期
    private Integer pageIndex;
    private Integer pageSize;
}
