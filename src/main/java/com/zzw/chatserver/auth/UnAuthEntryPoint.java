package com.zzw.chatserver.auth;

import com.zzw.chatserver.common.R;
import com.zzw.chatserver.common.ResultEnum;
import com.zzw.chatserver.utils.ResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

//未认证时执行这个类
public class UnAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ResponseUtil.out(response, R.error().resultEnum(ResultEnum.USER_NEED_AUTHORITIES));
    }
}
