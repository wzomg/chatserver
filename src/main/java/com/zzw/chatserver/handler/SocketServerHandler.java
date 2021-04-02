package com.zzw.chatserver.handler;

import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SocketServerHandler implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(SocketServerHandler.class);
    @Resource
    private SocketIOServer socketIOServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("socket server start");
        socketIOServer.start();
    }
}
