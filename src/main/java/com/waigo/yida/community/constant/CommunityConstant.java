package com.waigo.yida.community.constant;

/**
 * author waigo
 * create 2021-10-06 20:42
 */
public interface CommunityConstant {
    /**
     * 密码最短长度
     */
    int PASSWORD_MINIMUM_LENGTH = 8;
    /**
     * 密码最长长度
     */
    int PASSWORD_MAXIMUM_LENGTH = 64;
    /**
     * 帖子类型
     */
    int DISCUSS_POST = 1;
    /**
     * 评论类型
     */
    int COMMENT = 2;
    /**
     * 用户类型
     */
    int USER = 3;
    /**
     * 错误评论类型
     */
    int ERROR_COMMENT = 0;
    /**
     * 帖子评论一页显示几条
     */
    int DISCUSS_POST_PAGE_SIZE = 5;
    /**
     * 私信列表一页显示几条
     */
    int MESSAGE_PAGE_SIZE = 10;
    /**
     * 消息未读
     */
    int MESSAGE_UNREAD = 0;
    /**
     * 消息已读
     */
    int MESSAGE_READ = 1;
    /**
     * redis的key的分隔符
     */
    String REDIS_KEY_SPLIT = ":";
    /**
     * 粉丝列表一页显示几条
     */
    int FOLLOWER_LIST_PAGE_SIZE = 5;
    /**
     * 关注列表一页显示几条
     */
    int FOLLOWEE_LIST_PAGE_SIZE = 5;
    /**
     * kafka的主题常量
     */
    /**
     * 评论主题
     */
    String TOPIC_COMMENT = "comment";
    /**
     * 点赞主题
     */
    String TOPIC_LIKE = "like";
    /**
     * 关注主题
     */
    String TOPIC_FOCUS = "follow";
    /**
     * 更新帖子数据主题
     */
    String TOPIC_UPDATE_POST = "update_post";

    /**
     * 系统用户的ID
     */
    int SYSTEM_USER_ID = 1;

}
