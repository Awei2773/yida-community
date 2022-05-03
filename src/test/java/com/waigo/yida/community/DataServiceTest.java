package com.waigo.yida.community;

import com.waigo.yida.community.common.RedisClient;
import com.waigo.yida.community.service.DataService;
import com.waigo.yida.community.util.RedisKeyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;

/**
 * author waigo
 * create 2022-05-01 20:44
 */
@SpringBootTest
public class DataServiceTest {
    @Autowired
    DataService dataService;
    @Autowired
    RedisClient client;

    @Test
    public void caculateSegmentUVTest() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DAY_OF_MONTH, -7);
        Date start = instance.getTime();
        instance.add(Calendar.DAY_OF_MONTH, 16);
        Date end = instance.getTime();
        //存入测试数据
        dataService.recordUV("127.0.0.1");
        dataService.recordUV("127.0.0.2");
        dataService.recordUV("127.0.0.1");
        dataService.recordUV("127.0.0.2");
        dataService.recordUV("127.0.0.3");
        dataService.recordUV("127.0.0.4");
        //计算区间UV是否正确
        long l = dataService.caculateSegmentUV(start, end);
        System.out.println(l);
    }
    @Test
    public void testCaculateSegmentDAU(){
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DAY_OF_MONTH, -7);
        Date start = instance.getTime();
        instance.add(Calendar.DAY_OF_MONTH, 16);
        Date end = instance.getTime();
        dataService.recordDAU(2);
        dataService.recordDAU(5);
        dataService.recordDAU(6);
        dataService.recordDAU(6);
        dataService.recordDAU(5);
        String dailyAUKey = RedisKeyUtil.getDailyAUKey(start);
        String dailyAUKey1 = RedisKeyUtil.getDailyAUKey(end);
        client.setBit(dailyAUKey,100,true);
        client.setBit(dailyAUKey1,3,true);
        long l = dataService.caculateSegmentDAU(start, end);
        System.out.println(l);
        System.out.println();

    }
}
