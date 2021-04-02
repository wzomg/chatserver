package com.zzw.chatserver.service;


import com.mongodb.client.result.UpdateResult;
import com.zzw.chatserver.dao.ValidateMessageDao;
import com.zzw.chatserver.pojo.ValidateMessage;
import com.zzw.chatserver.pojo.vo.SimpleGroup;
import com.zzw.chatserver.pojo.vo.ValidateMessageResponseVo;
import com.zzw.chatserver.pojo.vo.ValidateMessageResultVo;
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
public class ValidateMessageService {
    @Resource
    private ValidateMessageDao validateMessageDao;

    @Resource
    private MongoTemplate mongoTemplate;

    public void changeFriendValidateNewsStatus(String validateMessageId, Integer status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(validateMessageId)));
        Update update = new Update();
        update.set("status", status);
        UpdateResult result = mongoTemplate.upsert(query, update, "validatemessages");
        // System.out.println("是否更新成功？" + result);
    }

    public void changeGroupValidateNewsStatus(String validateMessageId, Integer status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(validateMessageId)));
        Update update = new Update();
        update.set("status", status);
        UpdateResult result = mongoTemplate.upsert(query, update, "validatemessages");
        // System.out.println("是否更新成功？" + result);
    }


    public List<ValidateMessageResponseVo> getMyValidateMessageList(String userId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "groups",
                        "groupId",
                        "_id",
                        "groupList"
                ), Aggregation.match(Criteria.where("receiverId").is(new ObjectId(userId)))
        );
        List<ValidateMessageResultVo> validatemessages = mongoTemplate.aggregate(aggregation, "validatemessages", ValidateMessageResultVo.class).getMappedResults();
        // System.out.println("查询我的验证消息列表结果为：" + validatemessages);
        List<ValidateMessageResponseVo> responseVoList = new ArrayList<>();
        ValidateMessageResponseVo item;
        for (ValidateMessageResultVo son : validatemessages) {
            item = new ValidateMessageResponseVo();
            BeanUtils.copyProperties(son, item);
            if (son.getGroupId() != null && son.getGroupList() != null && son.getGroupList().size() > 0) {
                item.setGroupInfo(new SimpleGroup());
                item.getGroupInfo().setGid(son.getGroupList().get(0).getGroupId().toString());
                item.getGroupInfo().setTitle(son.getGroupList().get(0).getTitle());
            }
            responseVoList.add(item);
        }
        return responseVoList;
    }

    public ValidateMessage findValidateMessage(String roomId, Integer status, Integer validateType) {
        return validateMessageDao.findValidateMessageByRoomIdAndStatusAndValidateType(roomId, status, validateType);
    }


    public ValidateMessage addValidateMessage(ValidateMessage validateMessage) {
        ValidateMessage res = findValidateMessage(validateMessage.getRoomId(), 0, validateMessage.getValidateType()); //查出未处理状态
        if (res == null)
            return validateMessageDao.save(validateMessage);
        // System.out.println("查到的验证消息为：" + res);
        return null;
    }
}
