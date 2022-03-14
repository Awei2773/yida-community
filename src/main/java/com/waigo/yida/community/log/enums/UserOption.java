package com.waigo.yida.community.log.enums;

/**
 * author waigo
 * create 2021-10-08 16:49
 */

/**
 * 定义用户操作的类型
 */
public enum UserOption {
    LOGIN("登录"),
    REGISTER("注册"),
    ACTIVATE("激活"),
    CHANGE_PASSWORD("修改密码"),
    PUBLISH_POST("发布帖子"),
    SEND_COMMENT("发表评论"),
    LIKE(""),
    SEND_LETTER("发送私信"),
    GET_CONVERSATION("获取会话"),
    LOGOUT("退出登录");
    String name;
    UserOption(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }}
