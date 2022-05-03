package com.waigo.yida.community.util;

import com.waigo.yida.community.constant.CommunityConstant;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author waigo
 * create 2021-10-12 21:17
 */
public class RedisKeyUtil {
    /**
     * 1.点赞业务的key设计
     * like:entityType:entityId -->使用的bitmap,将对应的userId bit给点成1或0,1是指的点赞
     */
    public static final String LIKE_SERVICE_PREFIX = "LIKE";

    public static String getLikeKey(int entityType, int entityId) {
        return getKey(LIKE_SERVICE_PREFIX, entityType, entityId);
    }

    /**
     * 2.用户收到的赞key的设计 value使用string,计数器，每次incr decr就好了
     * like:userId
     * 就是用户收到的赞需要特别设计，其他实体收到的赞可以直接用上面的key的bitCount查出来
     */
    public static String getUserRecvLikesKey(int userId) {
        return getKey(LIKE_SERVICE_PREFIX, userId);
    }

    /**
     * 3.对实体的关注key的设计
     * follower:entityType:entityId --> zset,里面存的member是userId, score是关注时间
     * 这是从实体方向看粉丝的数量和具体粉丝
     */
    public static final String FOLLOWER_SERVICE_PREFIX = "FOLLOWER";

    public static String getFollowerKey(int entityType, int entityId) {
        return getKey(FOLLOWER_SERVICE_PREFIX, entityType, entityId);
    }

    /**
     * 4.用户关注的实体的设计,按照类别做关注的收集
     * followee:entityType:userId --> zset,里面存的member是entityId,score是关注时间
     */
    public static final String FOLLOWEE_SERVICE_PREFIX = "FOLLOWEE";

    public static String getFolloweeKey(int entityType, int userId) {
        return getKey(FOLLOWEE_SERVICE_PREFIX, entityType, userId);
    }

    private static String getKey(Object... part) {
        return StringUtils.join(part, CommunityConstant.REDIS_KEY_SPLIT);
    }

    private static final String DATA_MODULE = "data";
    private static final String DATA_UV = "uv";
    private static final String DATA_DAU = "dau";
    private static final String TIME_SPLIT = "to";
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    /**
     * 传入日期返回key
     *
     * @param date yyyyMMdd形式
     * @return
     */
    public static String getDailyUVKey(Date date) {
        return getKey(DATA_MODULE, DATA_UV, df.format(date));
    }

    public static String getDailyAUKey(Date date) {
        return getKey(DATA_MODULE, DATA_DAU, df.format(date));
    }

    public static String getSegmentUVkey(Date start, Date end) {
        return getKey(DATA_MODULE, DATA_UV, df.format(start), TIME_SPLIT, df.format(end));
    }

    public static String getSegmentDAUkey(Date start, Date end) {
        return getKey(DATA_MODULE, DATA_DAU, df.format(start), TIME_SPLIT, df.format(end));
    }
}
