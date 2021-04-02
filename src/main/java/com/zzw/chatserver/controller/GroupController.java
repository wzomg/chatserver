package com.zzw.chatserver.controller;

import com.zzw.chatserver.common.R;
import com.zzw.chatserver.common.ResultEnum;
import com.zzw.chatserver.pojo.Group;
import com.zzw.chatserver.pojo.vo.*;
import com.zzw.chatserver.service.GroupService;
import com.zzw.chatserver.service.GroupUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Resource
    private GroupUserService groupUserService;

    @Resource
    private GroupService groupService;

    /**
     * 根据用户名获取我的群聊列表
     */
    @GetMapping("/getMyGroupList")
    public R getMyGroupList(String username) {
        List<MyGroupResultVo> myGroupList = groupUserService.getGroupUsersByUserName(username);
        // System.out.println("我的群聊列表为：" + myGroupList);
        return R.ok().data("myGroupList", myGroupList);
    }

    /**
     * 获取最近的群聊
     */
    @PostMapping("/recentGroup")
    public R getRecentGroup(@RequestBody RecentGroupVo recentGroupVo) {
        // System.out.println("最近的群聊列表请求参数为：" + recentGroupVo);
        List<MyGroupResultVo> recentGroups = groupUserService.getRecentGroup(recentGroupVo);
        // System.out.println("最近的群聊列表为：" + recentGroups);
        return R.ok().data("recentGroups", recentGroups);
    }

    /**
     * 获取群聊详情
     */
    @GetMapping("/getGroupInfo")
    public R getGroupInfo(String groupId) {
        Group groupInfo = groupService.getGroupInfo(groupId);
        // System.out.println("查询出的群消息为：" + groupInfo);
        List<MyGroupResultVo> groupUsers = groupUserService.getGroupUsersByGroupId(groupId);
        // System.out.println("群聊详情为：" + groupUsers);
        return R.ok().data("groupInfo", groupInfo).data("users", groupUsers);
    }

    /**
     * 在客户端搜索群聊
     */
    @PostMapping("/preFetchGroup")
    public R searchGroup(@RequestBody SearchRequestVo requestVo) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 这个 principal 跟校验token时保存认证信息有关
        List<SearchGroupResponseVo> groupResponseVos = groupService.searchGroup(requestVo, userId);
        return R.ok().data("groupList", groupResponseVos);
    }

    /**
     * 创建群聊
     */
    @PostMapping("/createGroup")
    public R createGroup(@RequestBody CreateGroupRequestVo requestVo) {
        String groupCode = groupService.createGroup(requestVo);
        return R.ok().data("groupCode", groupCode);
    }

    /**
     * 获取所有群聊
     */
    @GetMapping("/all")
    public R getAllGroup() {
        List<SearchGroupResultVo> allGroup = groupService.getAllGroup();
        return R.ok().data("allGroup", allGroup);
    }

    /**
     * 退出群聊
     */
    @PostMapping("/quitGroup")
    public R quitGroup(@RequestBody QuitGroupRequestVo requestVo) {
        // System.out.println("退出群聊的请求参数为：" + requestVo);
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 这个 principal 跟校验token时保存认证信息有关
        if (!userId.equals(requestVo.getUserId()))
            return R.error().resultEnum(ResultEnum.ILLEGAL_OPERATION); //当前操作人不匹配，非法操作
        groupService.quitGroup(requestVo);
        return R.ok().message("操作成功");
    }
}
