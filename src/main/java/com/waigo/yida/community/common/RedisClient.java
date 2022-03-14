package com.waigo.yida.community.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * author waigo
 * create 2021-10-12 20:13
 */
@Component
public class RedisClient {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 将key的offset设置成0或者1
     * @param key
     * @param offset
     * @param value value?1:0
     * @return {@literal null} when used in pipeline / transaction.
     */
    public Boolean setBit(String key,long offset,boolean value){
        return stringRedisTemplate.opsForValue().setBit(key,offset,value);
    }

    /**
     * Get the bit value at {@code offset} of value at {@code key}.
     * @param key
     * @param offset
     * @return
     */
    public Boolean getBit(String key,long offset){
        return stringRedisTemplate.opsForValue().getBit(key,offset);
    }

    /**
     * 这个bitcount没有直接的api，得通过execute来执行
     * @param key bitmap类型的key
     * @return
     */
    public Long bitCount(String key){
        return stringRedisTemplate.execute((RedisCallback<Long>) connection
                -> connection.bitCount(key.getBytes()));
    }
    public Long increment(String key,long delta){
        return stringRedisTemplate.opsForValue().increment(key,delta);
    }
    public Long decrement(String key,long delta){
        return stringRedisTemplate.opsForValue().decrement(key,delta);
    }
    public Integer getNumber(String key){
        Object o = stringRedisTemplate.opsForValue().get(key);
        return (Integer) o;
    }
    public String getString(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 进行事务控制
     * @param session 会话中要做的事情，都会在一次会话中完成
     * @param <T>
     * @return
     */
    public <T> T transation(SessionCallback<T> session){
        return stringRedisTemplate.execute(session);
    }

    public Long zcard(String zsetKey) {
        return stringRedisTemplate.opsForZSet().zCard(zsetKey);
    }

    public Double zscore(String key, int member) {
        return stringRedisTemplate.opsForZSet().score(key,member);
    }

    /**
     * 包首不包尾
     * @param key
     * @param start
     * @param end
     */
    public Set<ZSetOperations.TypedTuple<String>> zrange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end-1);
    }
}
