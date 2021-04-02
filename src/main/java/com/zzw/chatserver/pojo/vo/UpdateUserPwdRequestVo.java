package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserPwdRequestVo {
    private String oldPwd;
    private String newPwd;
    private String reNewPwd;
    private String userId;
}
