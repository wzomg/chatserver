package com.zzw.chatserver.config;

import com.zzw.chatserver.auth.UnAuthEntryPoint;
import com.zzw.chatserver.filter.JwtLoginAuthFilter;
import com.zzw.chatserver.filter.JwtPreAuthFilter;
import com.zzw.chatserver.filter.KaptchaFilter;
import com.zzw.chatserver.handler.ChatLogoutSuccessHandler;
import com.zzw.chatserver.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private OnlineUserService onlineUserService;

    //加密器
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //加载 userDetailsService，用于从数据库中取用户信息
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    //不进行认证的路径，可以直接访问
    @Override
    public void configure(WebSecurity web) throws Exception {
        //巨坑，这里不能加上context-path:/chat，不然不能拦截
        web.ignoring().antMatchers("/user/getCode", "/sys/getFaceImages", "/user/register", "/sys/downloadFile",
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/superuser/login"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 开启跨域资源共享
        http.cors()
                .and().csrf().disable() // 关闭csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //关闭session（无状态）
                .and().authorizeRequests() //设置认证请求
                .antMatchers("/expression/**", "/face/**", "/img/**", "/uploads/**").permitAll() //放行静态资源
                .anyRequest().authenticated()
                .and().logout().logoutSuccessHandler(new ChatLogoutSuccessHandler()).and()
                // 添加到过滤链中，放在验证用户密码之前
                .addFilterBefore(new KaptchaFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)
                // 先是UsernamePasswordAuthenticationFilter用于login校验
                .addFilter(new JwtLoginAuthFilter(authenticationManager(), mongoTemplate, onlineUserService))
                // 再通过OncePerRequestFilter，对其它请求过滤
                .addFilter(new JwtPreAuthFilter(authenticationManager(), onlineUserService))
                .httpBasic().authenticationEntryPoint(new UnAuthEntryPoint()); //没有权限访问
    }
}
