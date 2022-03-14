package com.waigo.yida.community.common;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * author waigo
 * create 2021-10-23 20:59
 */
@Component
public class KafkaClient {
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    public void sendObject(String topic,Object msg){
        kafkaTemplate.send(topic,JSONObject.toJSONString(msg));
    }
}
