package com.waigo.yida.community.service;

/**
 * author waigo
 * create 2021-10-12 20:25
 */

import com.waigo.yida.community.common.KafkaClient;
import com.waigo.yida.community.common.RedisClient;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.service.impl.EventProducer;
import com.waigo.yida.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 点赞
 */
@Service
@SuppressWarnings("unchecked")
public class LikeService {
    @Autowired
    RedisClient redisClient;
    @Autowired
    EventProducer eventProducer;
    /**
     * userId给targetUserId的某个实体点赞，实体例如：帖子、评论、回复等等，肯定是属于某个用户的输出资料
     * @param entityType 这里评论和回复的type都是2，也就是系统定义的实体id
     * @param entityId
     * @param userId
     * @param targetUserId
     */
    public void like(int entityType, int entityId, int userId, int targetUserId) {
        final String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        final String usersLikeKey = RedisKeyUtil.getUserRecvLikesKey(targetUserId);
        List res = null;
        while(res==null){//CAS操作，保证事务原子性，一致性
            res = redisClient.transation(new SessionCallback<List>() {
                @Override
                public List execute(@NonNull RedisOperations operations) throws DataAccessException {
                    //保证在此次操作期间用户这个点赞状态是不变的，CAS操作
                    operations.watch(likeKey);
                    Boolean check = operations.opsForValue().getBit(likeKey, userId);
                    int isLike = check == null || !check ? 0 : 1;
                    operations.multi();
                    if (isLike == 0) {
                        //1.点赞
                        operations.opsForValue().setBit(likeKey, userId, true);
                        //2.让用户拥有的赞加1
                        operations.opsForValue().increment(usersLikeKey, 1);
                        //3.发消息到Kafka
                        Event event = new Event();
                        event.setTopic(CommunityConstant.TOPIC_LIKE)
                                .setEntityId(entityId)
                                .setEntityType(entityType)
                                .setPostId(userId)
                                .setUserId(targetUserId);
                        eventProducer.publishEvent(event);
                    } else {
                        //3.取消点赞
                        operations.opsForValue().setBit(likeKey, userId, false);
                        //4.让用户拥有的赞减1
                        operations.opsForValue().decrement(usersLikeKey, 1);
                    }
                    return operations.exec();
                }
            });
        }
    }

    public long likeCount(int entityType, int entityId) {
        Long counts = redisClient.bitCount(RedisKeyUtil.getLikeKey(entityType, entityId));
        return counts == null ? 0 : counts;
    }

    public int isLike(int entityType, int entityId, int userId) {
        String key = RedisKeyUtil.getLikeKey(entityType, entityId);
        Boolean bit = redisClient.getBit(key, userId);
        return bit == null || !bit ? 0 : 1;
    }

    public int findUserLikeCount(int userId) {
        Integer number = redisClient.getNumber(RedisKeyUtil.getUserRecvLikesKey(userId));
        return number==null?0:number;
    }
}
