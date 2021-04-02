package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestVo {
    private String type;//待查字段名
    private String searchContent;//搜索字符串
    private Integer pageIndex;
    private Integer pageSize;
}
