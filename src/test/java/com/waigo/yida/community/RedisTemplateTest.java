package com.waigo.yida.community;

import com.waigo.yida.community.common.RedisClient;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.service.LikeService;
import com.waigo.yida.community.util.RedisKeyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * author waigo
 * create 2021-10-12 9:14
 */
@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedisClient redisClient;

    @Test
    public void stringTest(){
        String key = "user:name";
        redisTemplate.opsForValue().set(key,"lisa");
        System.out.println(redisTemplate.opsForValue().get(key));
    }
    @Test
    public void listTest(){
        String key = "user:attention:5000";
        redisTemplate.opsForList().leftPush(key,"猪猪侠");
        redisTemplate.opsForList().leftPush(key,"钢铁侠");
        redisTemplate.opsForList().leftPush(key,"绿箭侠");
        redisTemplate.opsForList().leftPush(key,"闪电侠");
        redisTemplate.opsForList().leftPush(key,"蝙蝠侠");
        List<String> range = redisTemplate.opsForList().range(key, 0, -1);
        System.out.println(range);
    }
    @Test
    public void hashTest(){
        String key = "post:like:5000";//前5000帖子的点赞数量
        redisTemplate.opsForHash().put(key,"100",1);
        redisTemplate.opsForHash().put(key,"1",3);
        redisTemplate.opsForHash().put(key,"5",2);
        redisTemplate.opsForHash().put(key,"4",6);
        User user = new User();
        user.setEmail("1617731215@qq.com");
        user.setSalt("23423jsdf");
        user.setActivationCode("3234jjsdf");
        redisTemplate.opsForHash().put(key,"6", user);
        redisTemplate.opsForHash().increment(key,"1",1);//点赞
        System.out.println(redisTemplate.opsForHash().get(key,"1"));
    }
    @Test
    public void zsetTest(){
        String key = "user:exam";
        redisTemplate.opsForZSet().add(key,"唐僧",50);
        redisTemplate.opsForZSet().add(key,"悟空",35);
        redisTemplate.opsForZSet().add(key,"沙僧",35);
        redisTemplate.opsForZSet().add(key,"八戒",40);
        System.out.println(redisTemplate.opsForZSet().rank(key,"悟空"));
        System.out.println(redisTemplate.opsForZSet().rank(key,"沙僧"));
    }
    @Autowired
    LikeService likeService;
    @Test
    public void multiTest(){
        /*long l = likeService.likeCount(CommunityConstant.DISCUSS_POST, 112);
        System.out.println(l);*/
        String userRecvLikesKey = RedisKeyUtil.getUserRecvLikesKey(158);
        Object o = redisTemplate.opsForValue().get(userRecvLikesKey);
        Integer s = (Integer) o;
        System.out.println(s);
    }
    @Test
    public void hyperloglogTest(){
        String dailyUVKey = RedisKeyUtil.getDailyUVKey(new Date());
        Date lastDayDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        String lastUVKey = RedisKeyUtil.getDailyUVKey(lastDayDate);
        redisTemplate.opsForHyperLogLog().add(dailyUVKey,"127.0.0.6");
        redisTemplate.opsForHyperLogLog().add(dailyUVKey,"127.0.0.1");
        redisTemplate.opsForHyperLogLog().add(dailyUVKey,"127.0.0.5");
        redisTemplate.opsForHyperLogLog().add(dailyUVKey,"127.0.0.2");
        redisTemplate.opsForHyperLogLog().add(lastUVKey,"127.0.0.1");
        redisTemplate.opsForHyperLogLog().add(lastUVKey,"127.0.0.1");
        redisTemplate.opsForHyperLogLog().add(lastUVKey,"127.0.0.4");
        redisTemplate.opsForHyperLogLog().add(lastUVKey,"127.0.0.3");
        redisTemplate.opsForHyperLogLog().size(dailyUVKey);
        String segmentUVkey = RedisKeyUtil.getSegmentUVkey(new Date(), lastDayDate);
        System.out.println(redisClient.hllUnion(segmentUVkey,dailyUVKey,lastUVKey));
        System.out.println(0);
    }
    @Test
    public void orAndExpireTest(){
        Date now = new Date();
        String dailyUVKey = RedisKeyUtil.getDailyUVKey(now);
        Date lastDayDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
       String lastDay = RedisKeyUtil.getDailyUVKey(lastDayDate);
        redisClient.setBit(dailyUVKey,1,true);
        redisClient.setBit(dailyUVKey,3,true);
        redisClient.setBit(dailyUVKey,5,true);
        redisClient.setBit(lastDay,2,true);
        redisClient.setBit(lastDay,4,true);
        redisClient.setBit(lastDay,6,true);
        String destKey = RedisKeyUtil.getSegmentUVkey(now, lastDayDate);
        Long orRes = redisClient.bitOprForOr(destKey,dailyUVKey,lastDay);
        orRes = redisClient.bitCount(destKey);
        System.out.println(orRes);//6
        redisClient.setExpire(destKey);//测试过期是否正确
    }
}
