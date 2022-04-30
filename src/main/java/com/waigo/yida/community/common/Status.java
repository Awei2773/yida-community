package com.waigo.yida.community.common;

import com.alibaba.fastjson.JSON;
import com.waigo.yida.community.constant.StatusCode;

import java.util.HashMap;

/**
 * author waigo
 * create 2021-10-03 12:54
 */
public class Status extends HashMap<String, Object> {
    private Status(int code) {
        this.put(CODE, code);
    }

    public static final String CODE = "code";
    public static final String MESSAGE = "message";

    /**
     * @return 业务成功时返回的状态码
     */
    public static Status success() {
        return new Status(StatusCode.SUCCESS);
    }

    /**
     * @return 业务失败时返回的状态码
     */
    public static Status failure() {
        return new Status(StatusCode.FAILURE);
    }

    public static Status otherFailure(int code) {
        return new Status(code);
    }

    public int getCode() {
        return (int) this.get(CODE);
    }

    public boolean isSuccess() {
        return this.getCode() == StatusCode.SUCCESS;
    }

    public void addAttribute(String key, Object value) {
        this.put(key, value);
    }

    //方便链式调用
    public Status lineAddAttribute(String key, Object value) {
        this.addAttribute(key, value);
        return this;
    }

    public Status addMessage(String message) {
        this.addAttribute(MESSAGE, message);
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
