package com.waigo.yida.community.service;

import com.waigo.yida.community.common.KafkaClient;
import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.common.RedisClient;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.service.impl.EventProducer;
import com.waigo.yida.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * author waigo
 * create 2021-10-15 21:58
 */
@Service
public class FollowService {
    @Autowired
    RedisClient redisClient;
    @Autowired
    EventProducer eventProducer;
    /**
     * 根据实体类别和用户ID查出此用户关注的这个类别的个数
     * @param userId
     * @param entityType
     * @return
     */
    public long findUserFolloweesCountByType(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        Long count = redisClient.zcard(followeeKey);
        return count==null?0:count;
    }

    /**
     * 根据实体Type和实体ID来查出这个实体的粉丝数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityFollowersCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Long count = redisClient.zcard(followerKey);
        return count==null?0:count;
    }

    /**
     * 查出userId对应的user是否是这个实体的粉丝
     * @param entityType
     * @param entityId
     * @param userId
     * @return
     */
    public boolean isFollower(int entityType, int entityId, int userId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisClient.zscore(followerKey,userId)!=null;
    }

    /**
     * userId对某个实体(entityType,entityId)进行关注
     * @param entityType
     * @param entityId
     * @param userId
     * @param fromUserId 这个实体所属的用户的Id
     */
    public void follow(int entityType, int entityId, int userId,int fromUserId) {
        final String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        final String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        redisClient.transation(new SessionCallback<List>() {
            @Override
            public List execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                //1.先让关注实体将userId添加进入自己的粉丝列表
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                //2.用户自己的关注列表加入实体Id
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                //3.将关注的消息发到kafka
                Event event = new Event();
                event.setTopic(CommunityConstant.TOPIC_FOCUS)
                        .setEntityId(entityId)
                        .setEntityType(entityType)
                        .setPostId(userId)
                        .setUserId(fromUserId);
                eventProducer.publishEvent(event);
                return operations.exec();
            }
        });
    }

    /**
     * 当前userId对这个实体进行取关
     * @param entityType
     * @param entityId
     * @param userId
     */
    public void unFollow(int entityType, int entityId, int userId) {
        final String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        final String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        redisClient.transation(new SessionCallback<List>() {
            @Override
            public List execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                //1.先让关注实体将userId从自己的粉丝列表删去
                operations.opsForZSet().remove(followerKey,userId);
                //2.用户自己的关注列表删去实体Id
                operations.opsForZSet().remove(followeeKey,entityId);
                return operations.exec();
            }
        });
    }

    /**
     * 查出这个实体的某页
     * @param entityType
     * @param entityId
     * @param page
     * @return
     */
    public Set<ZSetOperations.TypedTuple<String>> selectFollowerPage(int entityType, int entityId, Page page) {
        final String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisClient.zrange(followerKey,page.getOffset(),page.getOffset()+page.getPageSize());
    }

    /**
     * 查出这个用户关注的entityType类型的page页
     * @param entityType
     * @param userId
     * @param page
     * @return
     */
    public Set<ZSetOperations.TypedTuple<String>> selectFolloweePageByType(int entityType, int userId, Page page) {
        final String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return redisClient.zrange(followeeKey,page.getOffset(),page.getOffset()+page.getPageSize());
    }
}
