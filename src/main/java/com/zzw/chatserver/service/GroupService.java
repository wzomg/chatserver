package com.zzw.chatserver.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.zzw.chatserver.common.ConstValueEnum;
import com.zzw.chatserver.dao.AccountPoolDao;
import com.zzw.chatserver.dao.GroupDao;
import com.zzw.chatserver.dao.GroupUserDao;
import com.zzw.chatserver.pojo.AccountPool;
import com.zzw.chatserver.pojo.Group;
import com.zzw.chatserver.pojo.GroupUser;
import com.zzw.chatserver.pojo.vo.*;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class GroupService {
    @Resource
    private GroupDao groupDao;
    @Resource
    private GroupUserDao groupUserDao;
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private AccountPoolDao accountPoolDao;

    public Group getGroupInfo(String groupId) {
        Optional<Group> res = groupDao.findById(new ObjectId(groupId));
        return res.orElse(null);
    }

    public List<SearchGroupResponseVo> searchGroup(SearchRequestVo requestVo, String uid) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "users",
                        "holderUserId",
                        "_id",
                        "holderUsers"
                ), Aggregation.match(
                        Criteria.where(requestVo.getType()).regex(Pattern.compile("^.*" + requestVo.getSearchContent() + ".*$", Pattern.CASE_INSENSITIVE))
                ), Aggregation.skip(Long.valueOf(requestVo.getPageIndex() * requestVo.getPageSize())),
                Aggregation.limit((long) requestVo.getPageSize()),
                Aggregation.sort(Sort.Direction.DESC, "_id")
        );
        List<SearchGroupResultVo> results = mongoTemplate.aggregate(aggregation, "groups", SearchGroupResultVo.class).getMappedResults();
        List<SearchGroupResponseVo> groups = new ArrayList<>();
        SearchGroupResponseVo item;
        for (SearchGroupResultVo son : results) {
            //???????????????????????????????????????
            if (!son.getHolderUsers().get(0).getUid().equals(uid)) {
                item = new SearchGroupResponseVo();
                BeanUtils.copyProperties(son, item);
                item.setGid(son.getId());
                BeanUtils.copyProperties(son.getHolderUsers().get(0), item.getHolderUserInfo());
                groups.add(item);
            }
        }
        return groups;
    }

    public String createGroup(CreateGroupRequestVo requestVo) {
        AccountPool accountPool = new AccountPool();
        accountPool.setType(2);//????????????
        accountPool.setStatus(1);//???????????????????????????????????????????????????
        accountPoolDao.save(accountPool);
        //================ ?????????????????????
        Group group = new Group();
        if (requestVo.getTitle() != null) group.setTitle(requestVo.getTitle());
        if (requestVo.getDesc() != null) group.setDesc(requestVo.getDesc());
        if (requestVo.getImg() != null) group.setImg(requestVo.getImg());
        group.setHolderName(requestVo.getHolderName());
        group.setHolderUserId(new ObjectId(requestVo.getHolderUserId()));
        //???????????????code+?????????
        group.setCode(String.valueOf(accountPool.getCode() + ConstValueEnum.INITIAL_NUMBER));
        groupDao.save(group);
        //============== ??????????????????
        GroupUser groupUser = new GroupUser();
        groupUser.setGroupId(group.getGroupId());
        groupUser.setUserId(group.getHolderUserId());
        groupUser.setUsername(group.getHolderName());
        groupUser.setHolder(1);
        groupUserDao.save(groupUser);
        //=====??????groups???gid?????????
        Update update = new Update();
        update.set("gid", group.getGroupId().toString());
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(group.getGroupId()));
        mongoTemplate.upsert(query, update, Group.class);
        return group.getCode();
    }

    public List<SearchGroupResultVo> getAllGroup() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "users",
                        "holderUserId",
                        "_id",
                        "holderUsers"
                )
        );
        AggregationResults<SearchGroupResultVo> groups = mongoTemplate.aggregate(aggregation, "groups", SearchGroupResultVo.class);
        return groups.getMappedResults();
    }

    //???????????????????????????????????????
    @Transactional
    //??????????????????
    public void quitGroup(QuitGroupRequestVo requestVo) {
        //?????????????????????????????????
        if (requestVo.getHolder() == 1) { // ?????????
            //1????????????????????????????????????(groupmessages)
            delGroupAllMessagesByGroupId(requestVo.getGroupId());
            //2???????????????????????????????????????groupusers???
            delGroupAllUsersByGroupId(requestVo.getGroupId());
            //3??????????????????????????????groups???
            groupDao.deleteById(new ObjectId(requestVo.getGroupId()));
        } else { // ????????????
            //1???????????????????????????????????????????????????
            delGroupMessagesByGroupIdAndSenderId(requestVo.getGroupId(), requestVo.getUserId());
            //2????????????????????????????????????
            delGroupUserByGroupIdAndUserId(requestVo.getGroupId(), requestVo.getUserId());
            //3?????????????????????1
            decrGroupUserNum(requestVo.getGroupId());
        }
    }

    private void delGroupAllMessagesByGroupId(String groupId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(groupId));
        DeleteResult groupmessages = mongoTemplate.remove(query, "groupmessages");
        // System.out.println("???????????????????????????????????????" + groupmessages.getDeletedCount());
    }

    private void delGroupAllUsersByGroupId(String groupId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("groupId").is(new ObjectId(groupId)));
        DeleteResult groupusers = mongoTemplate.remove(query, "groupusers");
        // System.out.println("???????????????????????????????????????" + groupusers.getDeletedCount());
    }

    private void delGroupMessagesByGroupIdAndSenderId(String groupId, String senderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(groupId).and("senderId").is(new ObjectId(senderId)));
        DeleteResult groupmessages = mongoTemplate.remove(query, "groupmessages");
        // System.out.println("????????????????????????????????????????????????" + groupmessages.getDeletedCount());
    }

    private void delGroupUserByGroupIdAndUserId(String groupId, String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("groupId").is(new ObjectId(groupId)).and("userId").is(new ObjectId(userId)));
        DeleteResult groupusers = mongoTemplate.remove(query, "groupusers");
        // System.out.println("?????????????????????????????????" + groupusers.getDeletedCount());
    }

    private void decrGroupUserNum(String gid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(gid)));
        Update update = new Update();
        update.inc("userNum", -1); //??????????????????1
        UpdateResult groups = mongoTemplate.upsert(query, update, "groups");
        // System.out.println("??????????????????1???????????????" + groups.getModifiedCount());
    }
}
