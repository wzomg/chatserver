package com.zzw.chatserver.controller;

import com.zzw.chatserver.common.R;
import com.zzw.chatserver.pojo.vo.HistoryMsgRequestVo;
import com.zzw.chatserver.pojo.vo.IsReadMessageRequestVo;
import com.zzw.chatserver.pojo.vo.SingleHistoryResultVo;
import com.zzw.chatserver.pojo.vo.SingleMessageResultVo;
import com.zzw.chatserver.service.SingleMessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/singleMessage")
public class SingleMessageController {
    @Resource
    private SingleMessageService singleMessageService;

    /**
     * 获取好友之间的最后一条聊天记录
     */
    @GetMapping("/getLastMessage")
    public R getLastMessage(String roomId) {
        SingleMessageResultVo lastMessage = singleMessageService.getLastMessage(roomId);
        return R.ok().data("singleLastMessage", lastMessage);
    }

    /**
     * 获取最近的单聊消息
     */
    @GetMapping("/getRecentSingleMessages")
    public R getRecentSingleMessages(String roomId, Integer pageIndex, Integer pageSize) {
        List<SingleMessageResultVo> recentMessage = singleMessageService.getRecentMessage(roomId, pageIndex, pageSize);
        return R.ok().data("recentMessage", recentMessage);
    }

    /**
     * 当用户在切换会话阅读消息后，标记该消息已读
     */
    @PostMapping("/isRead")
    public R userIsReadMessage(@RequestBody IsReadMessageRequestVo ivo) {
        singleMessageService.userIsReadMessage(ivo);
        return R.ok();
    }

    /**
     * 获取单聊历史记录
     */
    @PostMapping("/historyMessage")
    public R getSingleHistoryMessages(@RequestBody HistoryMsgRequestVo historyMsgVo) {
        // System.out.println("查看历史消息的请求参数为：" + historyMsgVo);
        SingleHistoryResultVo singleHistoryMsg = singleMessageService.getSingleHistoryMsg(historyMsgVo);
        return R.ok().data("total", singleHistoryMsg.getTotal()).data("msgList", singleHistoryMsg.getMsgList());
    }
}
