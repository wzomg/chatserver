package com.zzw.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserConfigureRequestVo {
    private Double opacity = 0.75D; //聊天框透明度
    private Integer blur = 10; //模糊度
    private String bgImg = "abstract"; //背景图种类名
    private String customBgImgUrl = ""; //自定义的背景图访问链接
    private String notifySound = "default"; //提示音
    private String color = "#000"; //字体颜色
    private String bgColor = "#fff"; //背景颜色
}
