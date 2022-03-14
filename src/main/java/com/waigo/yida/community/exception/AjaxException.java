package com.waigo.yida.community.exception;

/**
 * author waigo
 * create 2021-10-08 18:27
 */

import com.waigo.yida.community.util.R;

/**
 * 自定义异常类，用于业务的异常处理
 */
public class AjaxException extends RuntimeException{
    private int code;
    /**
     * ajax请求异常下返回的错误json数据
     */
    private R res;

    public AjaxException(int code, R res) {
        this.code = code;
        this.res = res;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public R getRes() {
        return res;
    }

    public void setRes(R res) {
        this.res = res;
    }
}
