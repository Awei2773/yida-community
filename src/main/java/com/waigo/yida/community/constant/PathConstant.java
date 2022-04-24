package com.waigo.yida.community.constant;

/**
 * author waigo
 * create 2022-03-23 23:47
 */
public interface PathConstant {
    /**
     * 登录相关的url
     */
    String LOGIN_PAGE = "/login.html";
    String INDEX = "/index";
    String INDEX_DEFAULT = "/";
    String INDEX_PAGE = "/index.html";
    String LOGIN_PROCESSING = "/login";
    String LOGOUT_PROCESSING = "/logout";
    /**
     * 评论相关URL
     */
    String ADD_COMMENT = "/comment/post";
    /**
     * 帖子相关URL
     */
    String ADD_DISCUSS_POST = "/discuss/post";
    String GET_DISCUSS_POST = "/discuss/get/*";
    String LIKE_DISCUSS_POST = "/discuss/like";
    String DISCUSS_WRITE_PAGE = "/discuss/write.html";
    /**
     * 关注相关URL
     */
    String FOLLOW_SOMEONE = "/follow/follow";
    String UNFOLLOW_SOMEONE = "/follow/unFollow";
    String GET_FOLLOWER = "/follow/follower";
    String GET_FOLLOWEE = "/follow/followee";
    /**
     * 验证码相关URL
     */
    String GET_IMG_CAPTCHA = "/img/captcha";
    String GET_EMAIL_CAPTCHA = "/email/captcha";
    /**
     * 消息相关URL
     */
    String FRIEND_LETTER_PAGE = "/letter/friend";
    String FRIEND_LETTER_CONVERSATION = "/letter/friend/*";
    String POST_FRIEND_LETTER = "/letter/post";
    String FRIEND_LETTER_CONVERSATION_MORE_PAGE = "/letter/friend/*/ajax";
    /**
     * 通知相关URL
     */
    String GET_NOTICE_PAGE = "/notice";
    String NOTICE_DETAIL_PAGE = "/notice/notice-detail/*";
    /**
     * 注册相关URL
     */
    String GET_REGISTER_PAGE = "/register";
    String REGISTER = "/register";
    String PRINCIPAL_ACTIVE = "/activation/*/*";
    /**
     * 全文检索相关URL
     */
    String SEARCH = "/search";
    /**
     * 用户相关URL
     */
    String USER_SETTING = "/user/setting";
    String USER_UPLOAD_HEADER = "/user/upload/headUrl";
    String USER_GET_HEADER = "/user/header/*";
    String USER_PASSWORD_UPDATE = "/user/password/update";
    String USER_PASSWORD_FORGET_PAGE = "/user/forget";
    String USER_PASSWORD_FORGET_PROCESSING = "/user/forget/password";
    String USER_PROFILE_PAGE = "/user/profile/*";





}
