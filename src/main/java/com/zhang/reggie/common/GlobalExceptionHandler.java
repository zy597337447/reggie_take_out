package com.zhang.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler{
    @ExceptionHandler(Exception.class)
    public R<String> exceptionHandler(Exception ex){
        log.error(ex.getMessage());
        return R.error("失败了");
    }
}
