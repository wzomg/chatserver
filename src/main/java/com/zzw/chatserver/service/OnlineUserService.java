package com.zzw.chatserver.service;

import com.zzw.chatserver.pojo.vo.SimpleUser;
import com.zzw.chatserver.utils.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * 用 string 数据结构维护一个 clientId -> user
 * 用 set 维护所有的 uid
 */

@Service
public class OnlineUserService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void addClientIdToSimpleUser(String clientId, SimpleUser simpleUser) {
        //将客户端id 和 用户信息绑定在一起
        String clientKey = RedisKeyUtil.getClientKey(clientId);
        //设置一下过期时间，以防该key一直存在（其实是可以不用设置的）
        redisTemplate.opsForValue().set(clientKey, simpleUser, 60 * 60 * 24, TimeUnit.SECONDS);
        // 将 uid 添加到 set 集合中
        String onlineUidSetKey = RedisKeyUtil.getOnlineUidSetKey();
        redisTemplate.opsForSet().add(onlineUidSetKey, simpleUser.getUid());
    }

    public Set<Object> getOnlineUidSet() {
        String onlineUidSetKey = RedisKeyUtil.getOnlineUidSetKey();
        return redisTemplate.opsForSet().members(onlineUidSetKey);
    }

    public SimpleUser getSimpleUserByClientId(String clientId) {
        String clientKey = RedisKeyUtil.getClientKey(clientId);
        Object o = redisTemplate.opsForValue().get(clientKey);
        return o == null ? null : (SimpleUser) o;
    }

    public void removeClientAndUidInSet(String clientId, String uid) {
        // 删除该客户端信息
        String clientKey = RedisKeyUtil.getClientKey(clientId);
        redisTemplate.delete(clientKey);

        // 删除在线用户列表的信息
        String onlineUidSetKey = RedisKeyUtil.getOnlineUidSetKey();
        redisTemplate.opsForSet().remove(onlineUidSetKey, uid);
    }

    public int countOnlineUser() {
        String onlineUidSetKey = RedisKeyUtil.getOnlineUidSetKey();
        Set<Object> members = redisTemplate.opsForSet().members(onlineUidSetKey);
        return members == null ? 0 : members.size();
    }

    public boolean checkCurUserIsOnline(String uid) {
        String onlineUidSetKey = RedisKeyUtil.getOnlineUidSetKey();
        Boolean res = redisTemplate.opsForSet().isMember(onlineUidSetKey, uid);
        return res == null ? false : res;
    }
}
