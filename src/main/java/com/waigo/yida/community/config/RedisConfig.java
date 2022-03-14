package com.waigo.yida.community.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * author waigo
 * create 2021-10-12 9:08
 */
@Configuration
public class RedisConfig {
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory){
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        //1.使用RedisSerializer下的json序列化器
        //redisTemplate.setHashValueSerializer(RedisSerializer.json());
        //2.使用FastJson的json序列化器，效率较高
        GenericFastJsonRedisSerializer fastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        //设置hash的value的序列化器，因为hash的value是可以设置Object类型的序列化后的字符串的
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        //设置完了之后需要调用一下afterPropertiesSet
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
