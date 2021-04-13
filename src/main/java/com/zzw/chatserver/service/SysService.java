package com.zzw.chatserver.service;

import com.zzw.chatserver.dao.SysDao;
import com.zzw.chatserver.pojo.FeedBack;
import com.zzw.chatserver.pojo.SensitiveMessage;
import com.zzw.chatserver.pojo.SystemUser;
import com.zzw.chatserver.pojo.vo.FeedBackResultVo;
import com.zzw.chatserver.pojo.vo.SensitiveMessageResultVo;
import com.zzw.chatserver.pojo.vo.SystemUserResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysService {
    @Resource
    private SysDao sysDao;

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 项目一启动就检查一下
     */
    public void notExistThenAddSystemUser(SystemUser user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(user.getCode()));
        SystemUser one = mongoTemplate.findOne(query, SystemUser.class);
        if (one == null) {
            sysDao.save(user);
        }
    }

    public List<SystemUserResponseVo> getSysUsers() {
        List<SystemUser> systemUsers = sysDao.findAll();
        List<SystemUserResponseVo> res = new ArrayList<>();
        SystemUserResponseVo item;
        for (SystemUser son : systemUsers) {
            item = new SystemUserResponseVo();
            BeanUtils.copyProperties(son, item);
            item.setSid(son.getId().toString());
            res.add(item);
        }
        return res;
    }

    public void addFeedBack(FeedBack feedBack) {
        mongoTemplate.insert(feedBack, "feedbacks");
    }

    public void addSensitiveMessage(SensitiveMessage sensitiveMessage) {
        mongoTemplate.insert(sensitiveMessage, "sensitivemessages");
    }

    public List<SensitiveMessageResultVo> getSensitiveMessageList() {
        return mongoTemplate.findAll(SensitiveMessageResultVo.class, "sensitivemessages");
    }


    public List<FeedBackResultVo> getFeedbackList() {
        return mongoTemplate.findAll(FeedBackResultVo.class, "feedbacks");
    }
}
