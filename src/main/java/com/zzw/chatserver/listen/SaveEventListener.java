package com.zzw.chatserver.listen;

import com.zzw.chatserver.annon.AutoIncKey;
import com.zzw.chatserver.pojo.AccountPool;
import com.zzw.chatserver.pojo.SeqInfo;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;

//监听设置某个集合的主键值加1
@Component
public class SaveEventListener extends AbstractMongoEventListener<AccountPool> {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<AccountPool> event) {
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            if (field.isAnnotationPresent(AutoIncKey.class)) {//判断字段是否被自定义注解标识
                field.set(source, getNextId(source.getClass().getSimpleName()));//设置id
            }
        });
    }

    private Long getNextId(String collName) {
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update();
        update.inc("seqId", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        SeqInfo seq = mongoTemplate.findAndModify(query, update, options, SeqInfo.class);
        assert seq != null;
        return seq.getSeqId();
    }
}
