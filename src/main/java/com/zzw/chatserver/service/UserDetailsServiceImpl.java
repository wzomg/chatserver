package com.zzw.chatserver.service;

import com.zzw.chatserver.auth.entity.JwtAuthUser;
import com.zzw.chatserver.dao.UserDao;
import com.zzw.chatserver.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.findUserByUsernameOrCode(s, s);
        log.info("查询到的登录用户信息为：{}", user);
        if (user == null) throw new UsernameNotFoundException("该用户不存在！");
        JwtAuthUser jwtAuthUser = new JwtAuthUser();
        BeanUtils.copyProperties(user, jwtAuthUser);
        return jwtAuthUser;
    }
}
