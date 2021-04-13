package com.zzw.chatserver;

import com.alibaba.fastjson.JSON;
import com.sun.management.OperatingSystemMXBean;
import com.zzw.chatserver.pojo.AccountPool;
import com.zzw.chatserver.pojo.Group;
import com.zzw.chatserver.pojo.SystemUser;
import com.zzw.chatserver.pojo.vo.*;
import com.zzw.chatserver.service.*;
import com.zzw.chatserver.utils.FastDFSUtil;
import com.zzw.chatserver.utils.RedisKeyUtil;
import org.csource.common.MyException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringBootTest
class ChatServerApplicationTests {

    @Resource
    private GroupUserService groupUserService;

    @Resource
    private GroupService groupService;

    @Resource
    private AccountPoolService accountPoolService;

    @Resource
    private GroupMessageService groupMessageService;

    @Resource
    private GoodFriendService friendlyService;

    @Resource
    private ValidateMessageService validateMessageService;

    @Resource
    private SingleMessageService messageService;

    @Resource
    private SysService sysService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void initSystemUser() {
        SystemUser systemUser = new SystemUser();
        systemUser.setCode("111111");
        systemUser.setNickname("验证消息");
        systemUser.setStatus(1);
        sysService.notExistThenAddSystemUser(systemUser);
    }

    @Test
    void getMyGroup() {
        List<MyGroupResultVo> zzw = groupUserService.getGroupUsersByUserName("zhaoliu");
        System.out.println(JSON.toJSON(zzw).toString());
    }

    @Test
    void getGroupInfo() {
        Group groupInfo = groupService.getGroupInfo("604e9de2bcb9ec60ead96d6d");
        System.out.println(JSON.toJSON(groupInfo).toString());
    }

    @Test
    void searchGroup() {
        SearchRequestVo searchGroupVo = new SearchRequestVo("code", "1", 0, 3);
        List<SearchGroupResponseVo> searchGroupResultVos = groupService.searchGroup(searchGroupVo, "");
        System.out.println(JSON.toJSON(searchGroupResultVos));
    }

    @Test
    void saveAccount() {
        AccountPool accountPool = new AccountPool();
        accountPool.setStatus(1);
        accountPool.setType(2);
        accountPoolService.saveAccount(accountPool);
        System.out.println(accountPool);
    }

    @Test
    void getAllGroup() {
        List<SearchGroupResultVo> allGroup = groupService.getAllGroup();
        System.out.println(allGroup);
    }

    @Test
    void getGroupHistoryNews() {
        HistoryMsgRequestVo groupHistoryVo = new HistoryMsgRequestVo();
        groupHistoryVo.setRoomId("603712b92ac11d277c74c385");
        groupHistoryVo.setType("all");
        groupHistoryVo.setPageIndex(0);
        groupHistoryVo.setPageSize(10);
        groupHistoryVo.setQuery("");
        GroupHistoryResultVo groupHistoryMessages = groupMessageService.getGroupHistoryMessages(groupHistoryVo);
        System.out.println(JSON.toJSONString(groupHistoryMessages));
    }

    @Test
    void groupLastMessage() {
        GroupMessageResultVo lastMessage = groupMessageService.getGroupLastMessage("604e9de2bcb9ec60ead96d6d");
        System.out.println(JSON.toJSONString(lastMessage));
    }

    @Test
    void getRecentGroupMessage() {
        List<GroupMessageResultVo> groupMessage = groupMessageService.getRecentGroupMessages("602d262bc4df6c5608455719", 0, 15);
        System.out.println(JSON.toJSONString(groupMessage));
    }

    @Test
    void getMyFriendsList() {
        List<MyFriendListResultVo> myFriendsList = friendlyService.getMyFriendsList("6042f6447cecd466e3ee39c4");
        System.out.println(JSON.toJSONString(myFriendsList));
    }

    @Test
    void getRecentConversation() {
        RecentConversationVo recentConversationVo = new RecentConversationVo();
        recentConversationVo.setRecentFriendIds(Arrays.asList("60348cdf8adf11413064b23d", "603af4b503b7a85ce4eef236"));
        recentConversationVo.setUserId("602cec33a733f042a0c038cd");
        List<SingleRecentConversationResultVo> recentConversation = friendlyService.getRecentConversation(recentConversationVo);
        System.out.println(JSON.toJSONString(recentConversation));
    }

    @Test
    void changeValidateNewsStatus() {
        ValidateMessageResponseVo validateMessage = new ValidateMessageResponseVo();
        validateMessageService.changeFriendValidateNewsStatus("", 1);
    }

    @Test
    void getLastMessage() {
        SingleMessageResultVo lastMessage = messageService.getLastMessage("6042f6447cecd466e3ee39c4-604818dc6fc483569f660588");
        System.out.println(JSON.toJSON(lastMessage));
    }

    @Test
    void getRecentGroup() {
        RecentGroupVo groupVo = new RecentGroupVo();
        groupVo.setUserId("602cec33a733f042a0c038cd");
        groupVo.setGroupIds(Arrays.asList("602d262bc4df6c5608455719"));
        List<MyGroupResultVo> recentGroup = groupUserService.getRecentGroup(groupVo);
        System.out.println(JSON.toJSONString(recentGroup));
    }

    @Test
    void getRecentMessage() {
        List<SingleMessageResultVo> recentMessage = messageService.getRecentMessage("6042f7d47cecd466e3ee39c5-6042f6447cecd466e3ee39c4", 0, 15);
        System.out.println(JSON.toJSONString(recentMessage));
    }

    @Test
    void userIsReadMsg() {
        String roomId = "602cec33a733f042a0c038cd-60348cdf8adf11413064b23d";
        String userId = "602cec33a733f042a0c038cd";
        IsReadMessageRequestVo isReadMessageRequestVo = new IsReadMessageRequestVo(roomId, userId);
        messageService.userIsReadMessage(isReadMessageRequestVo);
    }

    @Test
    void getSingleHistoryMsg() {
        HistoryMsgRequestVo historyMsgVo = new HistoryMsgRequestVo();
        historyMsgVo.setRoomId("602cec33a733f042a0c038cd-60348cdf8adf11413064b23d");
        historyMsgVo.setType("all");
        historyMsgVo.setPageIndex(0);
        historyMsgVo.setPageSize(10);
        historyMsgVo.setQuery("");
        SingleHistoryResultVo singleHistoryMsg = messageService.getSingleHistoryMsg(historyMsgVo);
        System.out.println(JSON.toJSONString(singleHistoryMsg));
    }

    @Test
    void testUploadFile() throws IOException, MyException {
        String fileName = "D:\\图片\\ii.jpg";
        String fileUrl = FastDFSUtil.uploadFile(fileName);
        System.out.println(fileUrl);
    }

    @Test
    void testGetFileToken() throws UnsupportedEncodingException, NoSuchAlgorithmException, MyException {
        String fileId = "group1/M00/00/00/wKgAgGBLXOGAcCC6AAB4kmjqsMM090.jpg";
        String fileUrl = FastDFSUtil.getToken(fileId);
        System.out.println(fileUrl);
    }

    @Test
    void testDownloadFile() throws IOException, MyException {
        String fileId = "group1/M00/00/00/wKgAgGBMYnWALuiVAAAlaygi3Nc99.xlsx";
        byte[] bytes = FastDFSUtil.downloadFile(fileId);
        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\17283\\Desktop\\01.xlsx"));
        fos.write(bytes);
        fos.close();
    }

    @Test
    void testRedisSet() {
        String onlineUserSetKey = RedisKeyUtil.getOnlineUidSetKey();
        redisTemplate.opsForSet().add(onlineUserSetKey, "1a");
        redisTemplate.opsForSet().add(onlineUserSetKey, "2a");
        redisTemplate.opsForSet().add(onlineUserSetKey, "3a");

        //redisTemplate.opsForSet().remove(onlineUserSetKey, "2a");

        Set<Object> members = redisTemplate.opsForSet().members(onlineUserSetKey);
        System.out.println("用户id列表：" + JSON.toJSONString(members));

        redisTemplate.delete(onlineUserSetKey);
        members = redisTemplate.opsForSet().members(onlineUserSetKey);
        System.out.println("删除key后的用户id列表：" + JSON.toJSONString(members));
    }

    @Test
    void testGetCPUAndMem() {
        //此 API JDK必须是1.8及以上的
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osmxb.getSystemCpuLoad();
        System.err.println("cpuLoad:" + cpuLoad);

        double totalvirtualMemory = osmxb.getTotalPhysicalMemorySize();
        double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        double value = freePhysicalMemorySize / totalvirtualMemory;
        System.err.println("men:" + (1 - value));
    }
}
