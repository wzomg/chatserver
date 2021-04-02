package com.zzw.chatserver.auth.entity;

import com.zzw.chatserver.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

//封装了用户信息，用于认证和鉴权。
public class JwtAuthUser extends User implements UserDetails {

    public JwtAuthUser() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
        //这里不能写成这样，因为判定账号是否锁定需要特定的逻辑放在认证成功的地方进行处理
        //return !super.getStatus().equals(ConstValueEnum.ACCOUNT_FREEZED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
