package com.zzw.chatserver.service;

import com.zzw.chatserver.dao.GoodFriendDao;
import com.zzw.chatserver.dao.UserDao;
import com.zzw.chatserver.pojo.GoodFriend;
import com.zzw.chatserver.pojo.User;
import com.zzw.chatserver.pojo.vo.*;
import com.zzw.chatserver.utils.DateUtil;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class GoodFriendService {
    @Resource
    private GoodFriendDao goodFriendDao;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private UserDao userDao;

    public List<MyFriendListResultVo> getMyFriendsList(String userId) {
        Aggregation aggregation1 = Aggregation.newAggregation( // 注意查询类型ObjectId
                Aggregation.match(Criteria.where("userM").is(new ObjectId(userId))),
                Aggregation.lookup(
                        "users",
                        "userY",
                        "_id",
                        "uList"
                )
        );
        Aggregation aggregation2 = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userY").is(new ObjectId(userId))),
                Aggregation.lookup(
                        "users",
                        "userM",
                        "_id",
                        "uList"
                )
        );
        List<MyFriendListResultVo> resList = new ArrayList<>();
        List<MyFriendListVo> results1 = mongoTemplate.aggregate(aggregation1, "goodfriends", MyFriendListVo.class).getMappedResults();
        List<MyFriendListVo> results2 = mongoTemplate.aggregate(aggregation2, "goodfriends", MyFriendListVo.class).getMappedResults();
        MyFriendListResultVo item;
        for (MyFriendListVo son : results1) {
            item = new MyFriendListResultVo();
            item.setCreateDate(son.getCreateDate());
            item.setNickname(son.getUList().get(0).getNickname());
            item.setPhoto(son.getUList().get(0).getPhoto());
            item.setSignature(son.getUList().get(0).getSignature());
            item.setId(son.getUList().get(0).getUserId().toString());
            item.setLevel(computedLevel(son.getUList().get(0).getOnlineTime()));
            item.setRoomId(userId + "-" + son.getUList().get(0).getUserId().toString());
            resList.add(item);
        }
        for (MyFriendListVo son : results2) {
            item = new MyFriendListResultVo();
            item.setCreateDate(son.getCreateDate());
            item.setNickname(son.getUList().get(0).getNickname());
            item.setPhoto(son.getUList().get(0).getPhoto());
            item.setSignature(son.getUList().get(0).getSignature());
            item.setId(son.getUList().get(0).getUserId().toString());
            item.setLevel(computedLevel(son.getUList().get(0).getOnlineTime()));
            item.setRoomId(son.getUList().get(0).getUserId().toString() + "-" + userId);
            resList.add(item);
        }
        return resList;
    }

    // 根据用户在线时间计算用户等级
    private Integer computedLevel(Long onlineTime) {
        double toHour = onlineTime.doubleValue() / 1000.0 / 60.0 / 60.0;
        int res = (int) Math.ceil(toHour);
        return Math.min(res, 8);
    }

    public List<SingleRecentConversationResultVo> getRecentConversation(RecentConversationVo recentConversationVo) {
        List<ObjectId> friendIds = new ArrayList<>();
        for (String son : recentConversationVo.getRecentFriendIds()) {
            friendIds.add(new ObjectId(son));
        }
        Criteria criteriaA = Criteria.where("userM").in(friendIds).and("userY").is(new ObjectId(recentConversationVo.getUserId()));
        Criteria criteriaB = Criteria.where("userY").in(friendIds).and("userM").is(new ObjectId(recentConversationVo.getUserId()));
        Criteria criteria = new Criteria();
        criteria.orOperator(criteriaA, criteriaB);
        Aggregation aggregation = Aggregation.newAggregation( // 注意查询类型ObjectId
                Aggregation.match(
                        /**
                         * $or: [
                         *       {userM: {$in: recentFriendIds}, userY: userId},
                         *       {userY: {$in: recentFriendIds}, userM: userId}
                         *     ]
                         */
                        criteria
                ),
                Aggregation.lookup(
                        "users",
                        "userY",
                        "_id",
                        "uList1"
                ),
                Aggregation.lookup(
                        "users",
                        "userM",
                        "_id",
                        "uList2"
                )
        );
        List<MyFriendListVo> friendlies = mongoTemplate.aggregate(aggregation, "goodfriends", MyFriendListVo.class).getMappedResults();
        // System.out.println("查询最近的好友列表为：" + friendlies);
        List<SingleRecentConversationResultVo> resultVoList = new ArrayList<>();
        SingleRecentConversationResultVo item;
        SimpleUser userM, userY;
        for (MyFriendListVo son : friendlies) {
            item = new SingleRecentConversationResultVo();
            //时间格式化
            item.setCreateDate(DateUtil.format(son.getCreateDate(), DateUtil.yyyy_MM_dd_HH_mm_ss));
            item.setId(son.getId());
            userM = new SimpleUser();
            userY = new SimpleUser();
            //保持原来添加好友时userM和userY的顺序，不然获取最近会话时roomId顺序会出错
            //根据 MyFriendListVo 中来判断
            if (son.getUList1().get(0).getUid().equals(son.getUserM())) {
                BeanUtils.copyProperties(son.getUList1().get(0), userM);
                BeanUtils.copyProperties(son.getUList2().get(0), userY);
            } else {
                BeanUtils.copyProperties(son.getUList1().get(0), userY);
                BeanUtils.copyProperties(son.getUList2().get(0), userM);
            }
            item.setUserM(userM);
            item.setUserY(userY);
            item.getUserM().setLevel(computedLevel(son.getUList1().get(0).getOnlineTime()));
            item.getUserY().setLevel(computedLevel(son.getUList2().get(0).getOnlineTime()));
            resultVoList.add(item);
        }
        return resultVoList;
    }

    public void addFriend(GoodFriend goodFriend) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userM").is(goodFriend.getUserM())
                .and("userY").is(goodFriend.getUserY())
        );
        GoodFriend one = mongoTemplate.findOne(query, GoodFriend.class);
        if (one == null) {
            // System.out.println("准备添加好友！");
            goodFriendDao.save(goodFriend);
            //添加好友时顺便将对方默认设置到 我的好友 这个分组
            modifyNewUserFenZu(goodFriend.getUserM().toString(), goodFriend.getUserY().toString());
            modifyNewUserFenZu(goodFriend.getUserY().toString(), goodFriend.getUserM().toString());
        }
    }

    private void modifyNewUserFenZu(String uid, String friendId) {
        User userInfo = getUser(uid);
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        friendFenZuMap.get("我的好友").add(friendId);
        //更新用户信息
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(uid)));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }


    public void deleteFriend(DelGoodFriendRequestVo requestVo) {
        //默认userM是主动删除者的ID，userY是被动删除者的ID
        Criteria criteriaA = Criteria.where("userY").is(new ObjectId(requestVo.getUserM())).and("userM").is(new ObjectId(requestVo.getUserY()));
        Criteria criteriaB = Criteria.where("userM").is(new ObjectId(requestVo.getUserM())).and("userY").is(new ObjectId(requestVo.getUserY()));
        Criteria criteria = new Criteria();
        criteria.orOperator(criteriaA, criteriaB);
        Query query = new Query();
        query.addCriteria(criteria);
        mongoTemplate.findAndRemove(query, GoodFriend.class);
        //根据 roomId 删除两者的聊天记录
        delSingleHistoryMessage(requestVo.getRoomId());
        //删除该好友表中对应的分组信息和备注信息，都要互相更改双方的分组信息
        delFriendFenZuAndBeiZhu(requestVo.getUserM(), requestVo.getUserY());
        delFriendFenZuAndBeiZhu(requestVo.getUserY(), requestVo.getUserM());
    }

    private void delSingleHistoryMessage(String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        mongoTemplate.remove(query, "singlemessages");
    }

    private User getUser(String uid) {
        return userDao.findById(new ObjectId(uid)).orElse(null);
    }

    /**
     * 删除 users 表中好友分组信息和备注西悉尼
     *
     * @param myId     当前登录的用户id
     * @param friendId 被删除好友 id
     */
    private void delFriendFenZuAndBeiZhu(String myId, String friendId) {
        User userInfo = getUser(myId);
        boolean flag = false;
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        // System.out.println("分组map：" + friendFenZuMap);
        for (Map.Entry<String, ArrayList<String>> item : friendFenZuMap.entrySet()) {
            Iterator<String> iterator = item.getValue().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(friendId)) {
                    //原来已经在某个分组就从中去掉
                    iterator.remove();
                    flag = true;//没必要再循环了
                    break;
                }
            }
            if (flag) break;
        }
        Map<String, String> friendBeiZhuMap = userInfo.getFriendBeiZhu();
        // System.out.println("备注map：" + friendBeiZhuMap);
        friendBeiZhuMap.remove(friendId);

        //更新用户信息
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(myId)));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap).set("friendBeiZhu", friendBeiZhuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }
}
