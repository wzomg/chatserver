package com.zzw.chatserver.service;

import com.zzw.chatserver.dao.GroupMessageDao;
import com.zzw.chatserver.pojo.GroupMessage;
import com.zzw.chatserver.pojo.vo.GroupHistoryResultVo;
import com.zzw.chatserver.pojo.vo.GroupMessageResultVo;
import com.zzw.chatserver.pojo.vo.HistoryMsgRequestVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class GroupMessageService {
    @Resource
    private GroupMessageDao groupMessageDao;

    @Resource
    private MongoTemplate mongoTemplate;

    public GroupHistoryResultVo getGroupHistoryMessages(HistoryMsgRequestVo groupHistoryVo) {
        // 创建复合查询对象
        Criteria cri1 = new Criteria();
        cri1.and("roomId").is(groupHistoryVo.getRoomId());
        //若查询条件是全部，则模糊匹配 message 或者 fileRawName
        //若查询条件不是全部，则设置搜索类型，并且模糊匹配 fileRawName
        Criteria cri2 = null;
        if (!groupHistoryVo.getType().equals("all")) {
            //若查询类型是文件或图片，则模糊匹配原文件名
            cri1.and("messageType").is(groupHistoryVo.getType())
                    .and("fileRawName").regex(Pattern.compile("^.*" + groupHistoryVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE));
        } else {
            cri2 = new Criteria().orOperator(Criteria.where("message").regex(Pattern.compile("^.*" + groupHistoryVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE)),
                    Criteria.where("fileRawName").regex(Pattern.compile("^.*" + groupHistoryVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE)));
        }
        if (groupHistoryVo.getDate() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(groupHistoryVo.getDate());
            calendar.add(Calendar.DATE, 1);
            Date tomorrow = calendar.getTime();
            // System.out.println("today：" + groupHistoryVo.getDate() + ", tomorrow：" + tomorrow);
            cri1.and("time").gte(groupHistoryVo.getDate()).lt(tomorrow);
        }
        // 创建查询对象
        Query query = new Query();
        if (cri2 != null) query.addCriteria(new Criteria().andOperator(cri1, cri2)); //最后两者是且的关系
        else query.addCriteria(cri1);
        // 统计总数
        long count = mongoTemplate.count(query, GroupMessageResultVo.class, "groupmessages");
        // 设置分页
        query.skip(groupHistoryVo.getPageIndex() * groupHistoryVo.getPageSize()); //页码
        query.limit(groupHistoryVo.getPageSize()); //每页显示数量
        List<GroupMessageResultVo> messageList = mongoTemplate.find(query, GroupMessageResultVo.class, "groupmessages");
        return new GroupHistoryResultVo(messageList, count);
    }

    public GroupMessageResultVo getGroupLastMessage(String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.DESC, "_id"));
        GroupMessageResultVo res = mongoTemplate.findOne(query, GroupMessageResultVo.class, "groupmessages");
        if (res == null)
            res = new GroupMessageResultVo();
        return res;
    }

    public List<GroupMessageResultVo> getRecentGroupMessages(String roomId, Integer pageIndex, Integer pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.DESC, "_id"))
                .skip(pageIndex * pageSize)
                .limit(pageSize);
        return mongoTemplate.find(query, GroupMessageResultVo.class, "groupmessages");
    }

    public void addNewGroupMessage(GroupMessage groupMessage) {
        groupMessageDao.save(groupMessage);
    }
}
