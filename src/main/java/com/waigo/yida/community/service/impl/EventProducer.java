package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.common.KafkaClient;
import com.waigo.yida.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author waigo
 * create 2022-02-20 17:50
 */

/**
 * 事件发起者
 */
@Service
public class EventProducer {
    @Autowired
    KafkaClient client;
    public void publishEvent(Event event){
        client.sendObject(event.getTopic(),event);
    }
}
