package com.itplh.hero.controller;

import lombok.Data;

@Data
public class Result<T> {

    private String code;
    private boolean success;
    private String message;
    private T data;
    private long timestamp;


    public Result(String code, boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result ok(T data) {
        return new Result("200", true, "Operation successful", data);
    }

    public static Result ok() {
        return ok(null);
    }

    public static <T> Result error(T data) {
        return new Result("400", false, "Operation failed", data);
    }

    public static Result error() {
        return error(null);
    }

}
