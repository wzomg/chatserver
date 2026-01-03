package com.zzw.chatserver.config;

import com.zzw.chatserver.auth.UnAuthEntryPoint;
import com.zzw.chatserver.filter.JwtLoginAuthFilter;
import com.zzw.chatserver.filter.JwtPreAuthFilter;
import com.zzw.chatserver.filter.KaptchaFilter;
import com.zzw.chatserver.handler.ChatLogoutSuccessHandler;
import com.zzw.chatserver.service.OnlineUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //不进行认证的路径，可以直接访问
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/user/getCode",
                "/sys/getFaceImages",
                "/user/register",
                "/sys/downloadFile",
                "/swagger-resources/**",
                "/webjars/**",
                "/v2/**",
                "/swagger-ui.html/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/superuser/login"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/expression/**", "/face/**", "/img/**", "/uploads/**").permitAll()
                .anyRequest().authenticated()
            )
            .logout(logout -> logout.logoutSuccessHandler(new ChatLogoutSuccessHandler()))
            .addFilterBefore(new KaptchaFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)
            .addFilter(new JwtLoginAuthFilter(authenticationManager, mongoTemplate, onlineUserService))
            .addFilter(new JwtPreAuthFilter(authenticationManager, onlineUserService))
            .httpBasic(basic -> basic.authenticationEntryPoint(new UnAuthEntryPoint()));

        return http.build();
    }
}
