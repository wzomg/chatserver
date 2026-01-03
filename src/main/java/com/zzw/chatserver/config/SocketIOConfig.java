package com.zzw.chatserver.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.zzw.chatserver.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {
    @Value("${socketio.port}")
    private Integer port;

    @Value("${socketio.workCount}")
    private int workCount;

    @Value("${socketio.allowCustomRequests}")
    private boolean allowCustomRequests;

    @Value("${socketio.upgradeTimeout}")
    private int upgradeTimeout;

    @Value("${socketio.pingTimeout}")
    private int pingTimeout;

    @Value("${socketio.pingInterval}")
    private int pingInterval;

    @Value("${socketio.maxFramePayloadLength}")
    private int maxFramePayloadLength;

    @Value("${socketio.maxHttpContentLength}")
    private int maxHttpContentLength;

    @Bean("socketIOServer")
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(port);
        com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);
        config.setWorkerThreads(workCount);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
        config.setMaxHttpContentLength(maxHttpContentLength);
        config.setMaxFramePayloadLength(maxFramePayloadLength);
        config.setTransports(Transport.WEBSOCKET);//指定传输协议为WebSocket
        
        // 添加鉴权监听器
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                // 从 URL 参数中获取 token
                String token = data.getSingleUrlParam("token");
                if (token == null || "".equals(token)) {
                    return false;
                }
                try {
                    // 解析并验证 token
                    Claims claims = JwtUtils.parseJwt(token);
                    String userId = claims.getSubject();
                    return userId != null;
                } catch (Exception e) {
                    return false;
                }
            }
        });

        return new SocketIOServer(config);
    }

    /**
     * 开启SocketIOServer注解支持，比如 @OnConnect、@OnEvent
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
