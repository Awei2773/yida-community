package com.waigo.yida.community.constant;

/**
 * author waigo
 * create 2021-10-04 11:00
 */
public interface AuthConstant {
    //单位是s
    int COMMON_EXPIRE_TIME = 3600*12;//12小时
    int REMEMBER_ME_EXPIRE_TIME = 3600*24*15;//15天
    /**
     * 系统角色，基于角色的权限控制
     * 0-普通用户; 1-超级管理员; 2-版主;
     */
    /**
     * 普通用户
     */
    String ROLE_USER = "USER";
    int TYPE_USER = 0;
    /**
     * 超级管理员
     */
    String ROLE_ROOT = "ROOT";
    int TYPE_ROOT = 1;
    /**
     * 版主
     */
    String ROLE_MODERATOR = "MODERATOR";
    int TYPE_MODERATOR = 2;
    /**
     * 未激活,status = 0
     */
    int LOCKED = 0;
    /**
     * 已激活,status = 1
     */
    int UNLOCKED = 1;

}
