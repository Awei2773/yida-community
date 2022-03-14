package com.waigo.yida.community.common;

import com.waigo.yida.community.constant.StatusCode;

import java.util.HashMap;

/**
 * author waigo
 * create 2021-10-03 12:54
 */
public class Status extends HashMap<String,Object> {
    private Status(int code){
        this.put(CODE,code);
    }
    public static final String CODE = "code";
    /**
     * @return 业务成功时返回的状态码
     */
    public static Status success(){
        return new Status(StatusCode.SUCCESS);
    }
    /**
     * @return 业务失败时返回的状态码
     */
    public static Status failure(){
        return new Status(StatusCode.FAILURE);
    }
    public int getCode(){
        return (int) this.get(CODE);
    }
    public boolean isSuccess(){
        return this.getCode()==StatusCode.SUCCESS;
    }
    public void addAttribute(String key,Object value){
        this.put(key,value);
    }
    //方便链式调用
    public Status lineAddAttribute(String key,Object value){
        this.put(key,value);
        return this;
    }
}
