package com.waigo.yida.community.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * author waigo
 * create 2021-10-12 20:13
 */
@Component
public class RedisClient {
    private static final long CACHE_TTL = 30L;
    private static final TimeUnit TTL_UNIT = TimeUnit.SECONDS;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 将key的offset设置成0或者1
     *
     * @param key
     * @param offset
     * @param value  value?1:0
     * @return {@literal null} when used in pipeline / transaction.
     */
    public Boolean setBit(String key, long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * Get the bit value at {@code offset} of value at {@code key}.
     *
     * @param key
     * @param offset
     * @return
     */
    public Boolean getBit(String key, long offset) {
        return stringRedisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 这个bitcount没有直接的api，得通过execute来执行
     *
     * @param key bitmap类型的key
     * @return
     */
    public Long bitCount(String key) {
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(key.getBytes()));
    }

    /**
     * 这里返回的是or之后的结果，而不是bitCount
     *
     * @param sourceKeys
     * @param destKey
     * @return
     */
    public Long bitOprForOr(String destKey, String... sourceKeys) {
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
            byte[][] sourceBytes = new byte[sourceKeys.length][];
            for (int i = 0; i < sourceKeys.length; i++) {
                sourceBytes[i] = sourceKeys[i].getBytes();
            }
            return connection.bitOp(RedisStringCommands.BitOperation.OR, destKey.getBytes(), sourceBytes);
        });
    }

    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    public Integer getNumber(String key) {
        Object o = stringRedisTemplate.opsForValue().get(key);
        return (Integer) o;
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Object getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 进行事务控制
     *
     * @param session 会话中要做的事情，都会在一次会话中完成
     * @param <T>
     * @return
     */
    public <T> T transation(SessionCallback<T> session) {
        return stringRedisTemplate.execute(session);
    }

    public Long zcard(String zsetKey) {
        return stringRedisTemplate.opsForZSet().zCard(zsetKey);
    }

    public Double zscore(String key, int member) {
        return stringRedisTemplate.opsForZSet().score(key, member);
    }

    /**
     * 包首不包尾
     *
     * @param key
     * @param start
     * @param end
     */
    public Set<ZSetOperations.TypedTuple<String>> zrange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end - 1);
    }

    /**
     * hyperLogLog
     **/
    public Long hllAdd(String key, String... e) {
        return stringRedisTemplate.opsForHyperLogLog().add(key, e);
    }

    /**
     * @param destKey
     * @param sourceKeys
     * @return 合并去重后的独立总数
     */
    public Long hllUnion(String destKey, String... sourceKeys) {
        return stringRedisTemplate.opsForHyperLogLog().union(destKey, sourceKeys);
    }
    public Long hllSize(String key){
        return stringRedisTemplate.opsForHyperLogLog().size(key);
    }

    public Boolean setExpire(String key) {
        return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.expire(key.getBytes(), TTL_UNIT.toSeconds(CACHE_TTL)));
    }
}
