package com.zzw.chatserver.controller;

import com.zzw.chatserver.common.R;
import com.zzw.chatserver.common.ResultEnum;
import com.zzw.chatserver.pojo.vo.DelGoodFriendRequestVo;
import com.zzw.chatserver.pojo.vo.MyFriendListResultVo;
import com.zzw.chatserver.pojo.vo.RecentConversationVo;
import com.zzw.chatserver.pojo.vo.SingleRecentConversationResultVo;
import com.zzw.chatserver.service.GoodFriendService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/goodFriend")
public class GoodFriendController {

    @Resource
    private GoodFriendService goodFriendService;

    /**
     * 查询我的好友列表
     */
    @GetMapping("/getMyFriendsList")
    public R getMyFriendsList(String userId) {
        List<MyFriendListResultVo> myFriendsList = goodFriendService.getMyFriendsList(userId);
        // System.out.println("我的好友列表为：" + myFriendsList);
        return R.ok().data("myFriendsList", myFriendsList);
    }

    /**
     * 查询最近好友列表
     */
    @PostMapping("/recentConversationList")
    public R getRecentConversationList(@RequestBody RecentConversationVo recentConversationVo) {
        List<SingleRecentConversationResultVo> resultVoList = goodFriendService.getRecentConversation(recentConversationVo);
        return R.ok().data("singleRecentConversationList", resultVoList);
    }

    /**
     * 删除好友
     */
    @DeleteMapping("/deleteGoodFriend")
    public R deleteGoodFriend(@RequestBody DelGoodFriendRequestVo requestVo) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 这个 principal 跟校验token时保存认证信息有关
        if (!userId.equals(requestVo.getUserM())) return R.error().resultEnum(ResultEnum.ILLEGAL_OPERATION); //不是本人，非法操作
        goodFriendService.deleteFriend(requestVo);
        return R.ok().message("删除好友成功");
    }
}
