package com.itplh.hero.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public Result exceptionHandler(Throwable e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }

}
