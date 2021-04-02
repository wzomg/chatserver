package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditFenZuRequestVo {
    private String oldFenZu;
    private String newFenZu;
    private String userId;
}
