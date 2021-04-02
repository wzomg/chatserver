package com.zzw.chatserver.filter;

import com.zzw.chatserver.common.R;
import com.zzw.chatserver.common.ResultEnum;
import com.zzw.chatserver.utils.CookieUtil;
import com.zzw.chatserver.utils.HttpServletRequestUtil;
import com.zzw.chatserver.utils.RedisKeyUtil;
import com.zzw.chatserver.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//验证码过滤器
public class KaptchaFilter extends GenericFilter {

    private RedisTemplate<String, String> redisTemplate;

    public KaptchaFilter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        // System.out.println("请求路径为：" + req.getServletPath());
        // System.out.println("请求方法为：" + req.getMethod());
        //这里不能写成 /chat/user/login，而应该是 /user/login（注册和登录都要用到验证码）
        if ("POST".equals(req.getMethod()) && ("/user/login".equals(req.getServletPath())) || ("/user/register".equals(req.getServletPath()))) {
            //从cookie中获取uuid
            String kaptchaOwner = CookieUtil.getValue(req, "kaptchaOwner");
            //获取填写的验证码，要用特殊的方法，不能通过 req.getParameter()
            //拷贝一份 request
            ServletRequest requestWrapper = new HttpServletRequestReplacedWrapper(req);
            String cvCode = HttpServletRequestUtil.getBodyTxt(requestWrapper, "cvCode");
            String kaptcha = null;
            if (StringUtils.isNotBlank(kaptchaOwner)) {
                // 验证码的 key 为 uuid， 值为 验证码
                String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
                Long expire = redisTemplate.getExpire(redisKey);
                // System.out.println("当前redis中剩余的过期时间为：" + expire);
                kaptcha = redisTemplate.opsForValue().get(redisKey);
            }
            // System.out.println("redis的验证码为：" + kaptcha);
            // System.out.println("填写的验证码为：" + cvCode);
            // System.out.println("我是分割线==================================================");
            if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(cvCode) || !kaptcha.equalsIgnoreCase(cvCode)) {
                ResponseUtil.out(resp, R.error().resultEnum(ResultEnum.KAPTCHA_TIME_OUT_OR_ERROR));
            } else filterChain.doFilter(requestWrapper, resp);
        } else {
            filterChain.doFilter(req, resp);
        }
    }
}
