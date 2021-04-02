package com.zzw.chatserver.service;

import com.zzw.chatserver.dao.GroupMessageDao;
import com.zzw.chatserver.dao.GroupUserDao;
import com.zzw.chatserver.pojo.Group;
import com.zzw.chatserver.pojo.GroupMessage;
import com.zzw.chatserver.pojo.GroupUser;
import com.zzw.chatserver.pojo.vo.*;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupUserService {
    @Resource
    private GroupUserDao groupUserDao;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private GroupMessageDao groupMessageDao;

    public List<MyGroupResultVo> getGroupUsersByUserName(String username) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is(username)),
                Aggregation.lookup(
                        "groups",
                        "groupId",
                        "_id",
                        "groupInfo"
                )
        );

        List<MyGroupResultVo> groupusers = mongoTemplate.aggregate(aggregation, "groupusers", MyGroupResultVo.class).getMappedResults();
        // System.out.println(groupusers);
        return groupusers;
    }

    public List<MyGroupResultVo> getGroupUsersByGroupId(String groupId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("groupId").is(new ObjectId(groupId))),
                Aggregation.lookup(
                        "users",
                        "userId",
                        "_id",
                        "userList"
                )
        );
        //根据群id去找所有的群成员信息
        List<MyGroupInfoQueryVo> queryVoList = mongoTemplate.aggregate(aggregation, "groupusers", MyGroupInfoQueryVo.class).getMappedResults();
        List<MyGroupResultVo> res = new ArrayList<>();
        MyGroupResultVo item;
        for (MyGroupInfoQueryVo son : queryVoList) {
            item = new MyGroupResultVo();
            BeanUtils.copyProperties(son, item);
            item.setUserInfo(new SimpleUser());
            BeanUtils.copyProperties(son.getUserList().get(0), item.getUserInfo());
            res.add(item);
        }
        return res;
    }

    public void addNewGroupUser(ValidateMessageResponseVo validateMessage) {
        GroupUser groupUser = groupUserDao.findGroupUserByUserIdAndGroupId(new ObjectId(validateMessage.getSenderId()), new ObjectId(validateMessage.getGroupInfo().getGid()));
        if (groupUser == null) {
            groupUser = new GroupUser();
            groupUser.setGroupId(new ObjectId(validateMessage.getGroupInfo().getGid()));
            groupUser.setUserId(new ObjectId(validateMessage.getSenderId()));
            groupUser.setUsername(validateMessage.getSenderName());
            groupUserDao.save(groupUser);

            Update update = new Update();
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(groupUser.getGroupId()));
            //群人数加1
            mongoTemplate.upsert(query, update.inc("userNum", 1), Group.class);
            //添加一条群消息
            GroupMessage groupMessage = new GroupMessage();
            groupMessage.setRoomId(groupUser.getGroupId().toString());
            //设置发送者id，为了退出群聊时比较方便删除!!!
            groupMessage.setSenderId(new ObjectId(validateMessage.getSenderId()));
            groupMessage.setMessageType("sys");
            groupMessage.setMessage(groupUser.getUsername() + "加入群聊");
            groupMessageDao.save(groupMessage);
        }
    }

    public List<MyGroupResultVo> getRecentGroup(RecentGroupVo recentGroupVo) {
        List<ObjectId> groupIds = new ArrayList<>();
        for (String son : recentGroupVo.getGroupIds()) {
            groupIds.add(new ObjectId(son));
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "groups",
                        "groupId",
                        "_id",
                        "groupList"
                ), Aggregation.match(Criteria.where("groupId").in(groupIds).and("userId").is(new ObjectId(recentGroupVo.getUserId())))
        );
        List<RecentGroupQueryVo> groupusers = mongoTemplate.aggregate(aggregation, "groupusers", RecentGroupQueryVo.class).getMappedResults();
        List<MyGroupResultVo> res = new ArrayList<>();
        MyGroupResultVo item;
        for (RecentGroupQueryVo son : groupusers) {
            item = new MyGroupResultVo();
            BeanUtils.copyProperties(son, item);
            item.setGroupInfo(son.getGroupList().get(0));
            res.add(item);
        }
        return res;
    }
}
