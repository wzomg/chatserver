package com.zzw.chatserver.service;

import com.zzw.chatserver.dao.SingleMessageDao;
import com.zzw.chatserver.pojo.SingleMessage;
import com.zzw.chatserver.pojo.vo.HistoryMsgRequestVo;
import com.zzw.chatserver.pojo.vo.IsReadMessageRequestVo;
import com.zzw.chatserver.pojo.vo.SingleHistoryResultVo;
import com.zzw.chatserver.pojo.vo.SingleMessageResultVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SingleMessageService {
    @Resource
    private SingleMessageDao singleMessageDao;
    @Resource
    private MongoTemplate mongoTemplate;

    public SingleMessageResultVo getLastMessage(String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.DESC, "_id"));
        SingleMessageResultVo message = mongoTemplate.findOne(query, SingleMessageResultVo.class, "singlemessages");
        if (message == null) {
            message = new SingleMessageResultVo();
            message.setRoomId(roomId);
            message.setMessageType("text");
            message.setMessage("");
        }
        return message;
    }

    //获取好友之间的聊天记录，通过房间id来获取，房间id是由两个好友id组成，所以是唯一的
    public List<SingleMessageResultVo> getRecentMessage(String roomId, Integer pageIndex, Integer pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.DESC, "_id"))
                .skip(pageIndex * pageSize)
                .limit(pageSize);
        return mongoTemplate.find(query, SingleMessageResultVo.class, "singlemessages");
    }

    //在用户切换到某条会话之后将给会话下的所有消息设置为已读
    public void userIsReadMessage(IsReadMessageRequestVo ivo) {
        Update update = new Update();
        update.addToSet("isReadUser", ivo.getUserId());
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(ivo.getRoomId()));
        mongoTemplate.updateMulti(query, update, "singlemessages");
    }

    public SingleHistoryResultVo getSingleHistoryMsg(HistoryMsgRequestVo historyMsgRequestVo) {
        Criteria cri1 = new Criteria();
        cri1.and("roomId").is(historyMsgRequestVo.getRoomId());
        //若查询条件是全部，则模糊匹配 message 或者 fileRawName
        //若查询条件不是全部，则设置搜索类型，并且模糊匹配 fileRawName
        Criteria cri2 = null;
        if (!historyMsgRequestVo.getType().equals("all")) {
            //若查询类型是文件或图片，则模糊匹配原文件名
            cri1.and("messageType").is(historyMsgRequestVo.getType())
                    .and("fileRawName").regex(Pattern.compile("^.*" + historyMsgRequestVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE));
        } else {
            cri2 = new Criteria().orOperator(Criteria.where("message").regex(Pattern.compile("^.*" + historyMsgRequestVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE)),
                    Criteria.where("fileRawName").regex(Pattern.compile("^.*" + historyMsgRequestVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE)));
        }
        if (historyMsgRequestVo.getDate() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(historyMsgRequestVo.getDate());
            calendar.add(Calendar.DATE, 1);
            Date tomorrow = calendar.getTime();
            cri1.and("time").gte(historyMsgRequestVo.getDate()).lt(tomorrow);
            // System.out.println("today：" + historyMsgRequestVo.getDate() + "，tomorrow：" + tomorrow);
        }
        // 创建查询对象
        Query query = new Query();
        if (cri2 != null) query.addCriteria(new Criteria().andOperator(cri1, cri2));
        else query.addCriteria(cri1);
        // 统计总数
        long total = mongoTemplate.count(query, SingleMessageResultVo.class, "singlemessages");
        // 设置分页
        query.skip(historyMsgRequestVo.getPageIndex() * historyMsgRequestVo.getPageSize()); //页码
        query.limit(historyMsgRequestVo.getPageSize()); //每页显示数量
        List<SingleMessageResultVo> messageList = mongoTemplate.find(query, SingleMessageResultVo.class, "singlemessages"); //必须带上集合名称
        return new SingleHistoryResultVo(messageList, total);
    }

    public void addNewSingleMessage(SingleMessage message) {
        singleMessageDao.save(message);
    }

}
