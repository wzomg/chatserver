package com.zzw.chatserver.service;

import com.zzw.chatserver.dao.SysDao;
import com.zzw.chatserver.pojo.FeedBack;
import com.zzw.chatserver.pojo.SystemUser;
import com.zzw.chatserver.pojo.vo.SystemUserResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
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

    public void addSystemUser(SystemUser user) {
        sysDao.save(user);
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
}
