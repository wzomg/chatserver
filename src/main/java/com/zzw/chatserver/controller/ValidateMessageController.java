package com.zzw.chatserver.controller;

import com.zzw.chatserver.common.R;
import com.zzw.chatserver.pojo.ValidateMessage;
import com.zzw.chatserver.pojo.vo.ValidateMessageResponseVo;
import com.zzw.chatserver.service.ValidateMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/validate")
public class ValidateMessageController {
    @Resource
    private ValidateMessageService validateMessageService;

    /**
     * 获取我的验证消息列表
     */
    @GetMapping("/getMyValidateMessageList")
    public R getMyValidateMessageList(String userId) {
        List<ValidateMessageResponseVo> validateMessageList = validateMessageService.getMyValidateMessageList(userId);
        return R.ok().data("validateMessageList", validateMessageList);
    }

    /**
     * 查询某条验证消息
     */
    @GetMapping("/getValidateMessage")
    public R getValidateMessage(String roomId, Integer status, Integer validateType) {
        ValidateMessage validateMessage = validateMessageService.findValidateMessage(roomId, status, validateType);
        return R.ok().data("validateMessage", validateMessage);
    }
}
