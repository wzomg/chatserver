package com.zzw.chatserver.listen;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.zzw.chatserver.common.ConstValueEnum;
import com.zzw.chatserver.filter.SensitiveFilter;
import com.zzw.chatserver.pojo.*;
import com.zzw.chatserver.pojo.vo.*;
import com.zzw.chatserver.service.*;
import com.zzw.chatserver.utils.DateUtil;
import com.zzw.chatserver.utils.SocketIoServerMapUtil;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Throwable.class)//当你的方法中抛出异常时，它会将事务回滚到进入此方法前的状态，数据库中的数据将不会改变。
public class SocketIoListener {
    private Logger logger = LoggerFactory.getLogger(SocketIoListener.class);

    @Resource
    private SocketIOServer socketIOServer;

    @Resource
    private GroupUserService groupUserService;

    @Resource
    private GoodFriendService goodFriendService;

    @Resource
    private ValidateMessageService validateMessageService;

    @Resource
    private GroupMessageService groupMessageService;

    @Resource
    private SingleMessageService singleMessageService;

    @Resource
    private UserService userService;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Resource
    private OnlineUserService onlineUserService;

    @Resource
    private SysService sysService;

    /**
     * map：clientId -> uid（用户判断用户上下线）
     * map：uid -> simpleUser（用于查询是否已经登录）
     */

    @OnConnect
    public void eventOnConnect(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        // System.out.println("客户端唯一标识为：" + client.getSessionId());
        logger.info("链接开启，urlParams：{}", urlParams);
    }

    //关闭当前网站或浏览器时都会执行这个方法
    @OnDisconnect
    public void eventOnDisConnect(SocketIOClient client) {
        logger.info("eventOnDisConnect ---> 客户端唯一标识为：{}", client.getSessionId());
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        //清除用户登录信息
        cleanLoginInfo(client.getSessionId().toString());
        logger.info("链接关闭，urlParams：{}", urlParams);
        // logger.info("剩余在线人数：{}", SocketIoServerMapUtil.getUidToUserMap().size());
        // logger.info("剩余在线人数：{}", onlineUserService.countOnlineUser());
        socketIOServer.getBroadcastOperations().sendEvent("onlineUser", onlineUserService.getOnlineUidSet());
    }

    private void cleanLoginInfo(String clientId) {
        SimpleUser simpleUser = onlineUserService.getSimpleUserByClientId(clientId);
        if (simpleUser != null) {
            onlineUserService.removeClientAndUidInSet(clientId, simpleUser.getUid());
            //设置下线用户的在线时长
            long onlineTime = DateUtil.getTimeDelta(simpleUser.getLastLoginTime(), new Date());
            userService.updateOnlineTime(onlineTime, simpleUser.getUid());
        }

        /*String uid = SocketIoServerMapUtil.getUid(clientId);
        if (uid != null) {
            SimpleUser simpleUser = SocketIoServerMapUtil.getUser(uid);
            // System.out.println("待删除的用户信息为：" + simpleUser);
            if (simpleUser != null) {
                //先删除 uid->User 的一对键值对
                SocketIoServerMapUtil.removeUser(uid);
                //设置下线用户的在线时长
                long onlineTime = DateUtil.getTimeDelta(simpleUser.getLastLoginTime(), new Date());
                userService.updateOnlineTime(onlineTime, uid);
            }
            //后删除 clientId -> uid 的一对键值对
            SocketIoServerMapUtil.removeUid(clientId);
        }*/
        printMessage();
    }

    private void printMessage() {
        //logger.info("当前在线客户端为：{}", SocketIoServerMapUtil.getClientToUidMap());
        //logger.info("在线用户的信息为：{}", SocketIoServerMapUtil.getUidToUserMap());
        logger.info("当前在线用户人数为：{}", onlineUserService.countOnlineUser());
    }

    //用户上线了
    @OnEvent("goOnline")
    public void goOnline(SocketIOClient client, User user) {
        logger.info("goOnline ---> user：{}", user);
        String clientId = client.getSessionId().toString();
        SimpleUser simpleUser = new SimpleUser();
        BeanUtils.copyProperties(user, simpleUser);

        onlineUserService.addClientIdToSimpleUser(clientId, simpleUser);
        // SocketIoServerMapUtil.putUid(clientId, user.getUid());
        // SocketIoServerMapUtil.putUser(user.getUid(), simpleUser);

        printMessage();

        //广播所有在线用户
        // socketIOServer.getBroadcastOperations().sendEvent("onlineUser", SocketIoServerMapUtil.getUidToUserMap());
        socketIOServer.getBroadcastOperations().sendEvent("onlineUser", onlineUserService.getOnlineUidSet());

    }

    //用户下线了
    @OnEvent("leave")
    public void leave(SocketIOClient client) {
        logger.info("leave ---> client：{}", client);
        //清除用户登录信息
        cleanLoginInfo(client.getSessionId().toString());
        //广播所有在线用户
        // socketIOServer.getBroadcastOperations().sendEvent("onlineUser", SocketIoServerMapUtil.getUidToUserMap());
        socketIOServer.getBroadcastOperations().sendEvent("onlineUser", onlineUserService.getOnlineUidSet());
    }

    @OnEvent("isReadMsg")
    public void isReadMsg(SocketIOClient client, UserIsReadMsgRequestVo requestVo) {
        logger.info("isReadMsg ---> requestVo：{}", requestVo);
        if (requestVo.getRoomId() != null) {
            //给同一房间的发送消息，这样才能同步状态
            Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(requestVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端，对于这个1v1
            for (SocketIOClient item : clients) {
                if (item != client) {
                    item.sendEvent("isReadMsg", requestVo);
                }
            }
        }
    }


    @OnEvent("join")
    public void join(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("加入房间号码：{} ---> conversationVo：{}", conversationVo.getRoomId(), conversationVo);
        //当前登录的客户端加入到指定房间
        client.joinRoom(conversationVo.getRoomId());
    }

    //接收到新的消息对消息类型所属的会话进行判断
    @OnEvent("sendNewMessage")
    public void sendNewMessage(SocketIOClient client, NewMessageVo newMessageVo) {
        logger.info("sendNewMessage ---> newMessageVo：{}", newMessageVo);
        if (newMessageVo.getConversationType().equals(ConstValueEnum.FRIEND)) {
            SingleMessage singleMessage = new SingleMessage();
            BeanUtils.copyProperties(newMessageVo, singleMessage);
            singleMessage.setSenderId(new ObjectId(newMessageVo.getSenderId()));
            // System.out.println("待插入的单聊消息为：" + singleMessage);
            singleMessageService.addNewSingleMessage(singleMessage);
        } else if (newMessageVo.getConversationType().equals(ConstValueEnum.GROUP)) {
            GroupMessage groupMessage = new GroupMessage();
            BeanUtils.copyProperties(newMessageVo, groupMessage);
            groupMessage.setSenderId(new ObjectId(newMessageVo.getSenderId()));
            // System.out.println("待插入的群聊消息为：" + groupMessage);
            groupMessageService.addNewGroupMessage(groupMessage);
        }
        //通知该房间收到消息接受到消息
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(newMessageVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveMessage", newMessageVo);
            }
        }
    }

    //发送验证消息，注意要防止重复添加，若status为0时
    @OnEvent("sendValidateMessage")
    public void sendValidateMessage(SocketIOClient client, ValidateMessage validateMessage) {
        logger.info("sendValidateMessage ---> validateMessage：{}", validateMessage);
        String[] res = sensitiveFilter.filter(validateMessage.getAdditionMessage());
        String filterContent = "";
        if (res != null) {
            filterContent = res[0];
            if (res[1].equals("1")) { //添加敏感词消息记录
                SensitiveMessage sensitiveMessage = new SensitiveMessage();
                sensitiveMessage.setRoomId(validateMessage.getRoomId());
                sensitiveMessage.setSenderId(validateMessage.getSenderId().toString());
                sensitiveMessage.setSenderName(validateMessage.getSenderName());
                sensitiveMessage.setMessage(validateMessage.getAdditionMessage());
                sensitiveMessage.setType(ConstValueEnum.VALIDATE);
                sensitiveMessage.setTime(validateMessage.getTime());
                sysService.addSensitiveMessage(sensitiveMessage);
            }
        }
        validateMessage.setAdditionMessage(filterContent);
        ValidateMessage addValidateMessage = validateMessageService.addValidateMessage(validateMessage);
        if (addValidateMessage != null) {//验证消息添加成功了才通知房间消息
            Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(validateMessage.getRoomId()).getClients(); //实际上同一房间只有2个客户端
            for (SocketIOClient item : clients) {
                if (item != client) {
                    item.sendEvent("receiveValidateMessage", validateMessage);
                }
            }
        }
    }

    //同意添加好友
    @OnEvent("sendAgreeFriendValidate")
    public void sendAgreeFriendValidate(SocketIOClient client, ValidateMessageResponseVo validateMessage) {
        logger.info("sendAgreeFriendValidate ---> validateMessage：{}", validateMessage);
        GoodFriend goodFriend = new GoodFriend();
        goodFriend.setUserM(new ObjectId(validateMessage.getSenderId()));
        goodFriend.setUserY(new ObjectId(validateMessage.getReceiverId()));
        goodFriendService.addFriend(goodFriend);

        // 用户同意加好友之后改变验证消息的状态
        validateMessageService.changeFriendValidateNewsStatus(validateMessage.getId(), 1);
        //========================
        String roomId = validateMessage.getRoomId(); //原本是接收者房间
        String receiverId = validateMessage.getReceiverId(); //接收者
        String senderId = validateMessage.getSenderId(); //发送者
        String senderRoomId = roomId.replaceAll(receiverId, senderId); //把接受者id替换为发送者id回到同意加好友的这方房间去更新我的好友列表
        //向roomId传送验证消息，不再通知接收者房间里的客户端，因为前端通过eventbus处理了
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(senderRoomId).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveAgreeFriendValidate", validateMessage); //通知发送者房间，除了当前客户端
            }
        }
    }

    //拒绝好友请求
    @OnEvent("sendDisAgreeFriendValidate")
    public void sendDisAgreeFriendValidate(SocketIOClient client, ValidateMessageResponseVo validateMessage) {
        logger.info("sendDisAgreeFriendValidate ---> validateMessage：{}", validateMessage);
        validateMessageService.changeFriendValidateNewsStatus(validateMessage.getId(), 2);
    }

    //删除好友转发一下消息
    @OnEvent("sendDelGoodFriend")
    public void sendDelGoodFriend(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("sendDelGoodFriend ---> conversationVo：{}", conversationVo);
        //转发一下消息
        //获取当前删除者的id
        //String uid = SocketIoServerMapUtil.getUid(client.getSessionId().toString());
        String uid = onlineUserService.getSimpleUserByClientId(client.getSessionId().toString()).getUid();
        //把会话id改为删除好友者，该会话的其他属性值不用管，这样传给被删除人的客户端时就显示为 被删除人也删除 删除人
        conversationVo.setId(uid);
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveDelGoodFriend", conversationVo); //通知被删好友去更新他的好友列表
            }
        }
    }

    //同意进群
    @OnEvent("sendAgreeGroupValidate")
    public void sendAgreeGroupValidate(SocketIOClient client, ValidateMessageResponseVo validateMessage) {
        logger.info("sendAgreeGroupValidate ---> validateMessage：{}", validateMessage);
        //添加群成员
        groupUserService.addNewGroupUser(validateMessage);
        //改变群验证消息的状态为1
        validateMessageService.changeGroupValidateNewsStatus(validateMessage.getId(), 1);
        //========================
        String roomId = validateMessage.getRoomId();
        String receiverId = validateMessage.getReceiverId();
        String senderId = validateMessage.getSenderId();
        String senderRoomId = roomId.replaceAll(receiverId, senderId); //把接受者id替换为发送者id回到同意加好友的这方房间去更新我的群列表
        //应该通知 请求加群的人 去更新他的群列表
        //这里应该换回系统通知，因为客户端房间里只加入了系统通知，新加的群号还没加入到房间呢
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(senderRoomId).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveAgreeGroupValidate", validateMessage); //只通知发送者房间
            }
        }
    }

    //拒绝进群
    @OnEvent("sendDisAgreeGroupValidate")
    public void sendDisAgreeGroupValidate(SocketIOClient client, ValidateMessageResponseVo validateMessage) {
        logger.info("sendDisAgreeGroupValidate ---> validateMessage：{}", validateMessage);
        validateMessageService.changeFriendValidateNewsStatus(validateMessage.getId(), 2);
    }

    //解散群或者退出群聊，则转发通知与这群关联的所有在线客户端 去更新我的群列表和最近会话中的群列表
    @OnEvent("sendQuitGroup")
    public void sendQuitGroup(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("sendQuitGroup ---> conversationVo：{}", conversationVo);
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveQuitGroup", conversationVo);
            }
        }
    }

    //转发申请
    @OnEvent("apply")
    public void apply(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("apply ---> roomId：{}", conversationVo.getRoomId());
        // System.out.println("apply user to，myNickname：" + conversationVo.getMyNickname());
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("apply", conversationVo);
            }
        }
    }

    //转发回复
    @OnEvent("reply")
    public void reply(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("reply ---> roomId：{}", conversationVo.getRoomId());
        // System.out.println("reply，myNickname：" + conversationVo.getMyNickname());
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("reply", conversationVo);
            }
        }
    }

    //转发 answer
    @OnEvent("1v1answer")
    public void answer(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("1v1answer ---> roomId：{}", conversationVo.getRoomId());
        // System.out.println("1v1answer，myNickname：" + conversationVo.getMyNickname());
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("1v1answer", conversationVo);
            }
        }
    }

    //转发 ICE，选取最佳的链接方式
    @OnEvent("1v1ICE")
    public void ICE(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("1v1ICE ---> roomId：{}", conversationVo.getRoomId());
        // System.out.println("1v1ICE，myNickname：" + conversationVo.getMyNickname());
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("1v1ICE", conversationVo);
            }
        }
    }

    //转发 Offer
    @OnEvent("1v1offer")
    public void offer(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("1v1offer ---> roomId：{}", conversationVo.getRoomId());
        // System.out.println("1v1offer，myNickname：" + conversationVo.getMyNickname());
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("1v1offer", conversationVo);
            }
        }
    }

    //转发 hangup
    @OnEvent("1v1hangup")
    public void hangup(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("1v1hangup ---> roomId：{}", conversationVo.getRoomId());
        // System.out.println("1v1hangup，myNickname：" + conversationVo.getMyNickname());
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("1v1hangup", conversationVo);
            }
        }
    }
}
