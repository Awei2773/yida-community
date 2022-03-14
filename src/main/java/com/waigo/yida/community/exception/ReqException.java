package com.waigo.yida.community.exception;

/**
 * author waigo
 * create 2021-10-08 18:35
 */


/**
 * 自定义异常，用于返回值是模板的业务异常处理,这里处理的是需要重定向找错误页面的异常，某些表单校验失败直接返回，不用重定向
 */
public class ReqException extends RuntimeException{
    private String msg;
    private String redirectPath;
    private int code;

    /**
     * 需要重定向使用这个
     * @param msg
     * @param redirectPath
     */
    public ReqException(String msg, String redirectPath) {
        this.msg = msg;
        this.redirectPath = redirectPath;
    }
    /**
     * 需要请求转发使用这个，确定error下存在code.html
     * @param msg
     * @param code
     */
    public ReqException(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
