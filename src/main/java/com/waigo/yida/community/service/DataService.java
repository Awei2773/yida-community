package com.waigo.yida.community.service;

import com.waigo.yida.community.common.RedisClient;
import com.waigo.yida.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 网站数据统计相关service
 * <p>
 * author waigo
 * create 2022-05-01 15:35
 */
@Service
public class DataService {
    @Autowired
    RedisClient redisClient;

    public void recordUV(String ip) {
        String dailyUVKey = RedisKeyUtil.getDailyUVKey(new Date());
        redisClient.hllAdd(dailyUVKey, ip);
    }

    /**
     * 计算一个区间的uv独立总数
     * (1)先查一次，有就返回，这里cache一层
     * (2)没有就利用calendar.setTime() getTime()遍历start ~ end,将这些hyperloglog合并，设置key过期时间五分钟
     * (3)将结果返回
     *
     * @param start
     * @param end
     * @return
     */
    public long caculateSegmentUV(Date start, Date end) {
        String segmentUVkey = RedisKeyUtil.getSegmentUVkey(start, end);
        Calendar instance = Calendar.getInstance();
        instance.setTime(start);
        //区间内的key都找出来
        ArrayList<String> sourceKeys = new ArrayList<>();
        while (!instance.getTime().after(end)) {
            sourceKeys.add(RedisKeyUtil.getDailyUVKey(instance.getTime()));
            instance.add(Calendar.DAY_OF_MONTH, 1);
        }
        //合并hll，算出独立总数
        Long segmentUV = redisClient.hllUnion(segmentUVkey, sourceKeys.toArray(new String[0]));
        //设置过期时间
        redisClient.setExpire(segmentUVkey);
        return segmentUV;
    }

    public void recordDAU(int userId) {
        String dailyAUKey = RedisKeyUtil.getDailyAUKey(new Date());
        redisClient.setBit(dailyAUKey, userId, true);
    }

    public long caculateSegmentDAU(Date start, Date end) {
        String segmentDAUkey = RedisKeyUtil.getSegmentDAUkey(start, end);
        //将这个区间的key找出来,要做or运算
        ArrayList<String> sourceKeys = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        instance.setTime(start);
        while (!instance.getTime().after(end)) {
            sourceKeys.add(RedisKeyUtil.getDailyAUKey(instance.getTime()));
            instance.add(Calendar.DAY_OF_MONTH, 1);
        }
        redisClient.bitOprForOr(segmentDAUkey, sourceKeys.toArray(new String[0]));
        //bitcount是答案，意思是有几个用户这些天内有登录过
        Long res = redisClient.bitCount(segmentDAUkey);
        redisClient.setExpire(segmentDAUkey);
        return res;
    }

}
