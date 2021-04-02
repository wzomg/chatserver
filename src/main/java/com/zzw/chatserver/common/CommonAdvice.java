package com.zzw.chatserver.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@RestController
public class CommonAdvice {
    private static Logger logger = LoggerFactory.getLogger(CommonAdvice.class);

    /**
     * 原始异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    R handleExceptionForErrorOne(Exception e, HttpServletRequest request) {
        logger.info("Exception message：{}", e.getMessage());
        logger.info("Exception from：{}", e.getCause());
        logger.info("Exception print：{}", e);
        return R.error().code((HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .message(e.getMessage());
    }


    /**
     * 自定义全局异常处理
     */
    @ExceptionHandler(GlobalException.class)
    R handleExceptionForErrorTwo(GlobalException e, HttpServletRequest request) {
        logger.info("MyException message：{}", e.getMessage());
        logger.info("MyException from：{}", e.getCause());
        logger.info("MyException print：{}", e);
        return R.error().code(e.getCode())
                .message(e.getMessage());
    }
}
