package com.waigo.yida.community.util;

/**
 * author waigo
 * create 2021-10-06 12:01
 */

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.waigo.yida.community.entity.User;

import java.util.HashMap;

/**
 * Ajax返回结果的封装对象，初始化需要传入code，msg
 */
public class R {
    //ordinal调整转成jsonString后的字段位置
    //name表示json中的key
    @JSONField(name="code",ordinal = 1)
    private int code;
    @JSONField(name="msg",ordinal = 2)
    private String msg;
    @JSONField(name="data",ordinal = 3)
    private HashMap<String,Object> data = new HashMap<>();
    private R(int code,String msg){
        this.code = code;
        this.msg = msg;
    }
    public R addAttribute(String key,Object value){
        this.data.put(key,value);
        return this;
    }
    public static R create(int code,String msg){
        return new R(code,msg);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
