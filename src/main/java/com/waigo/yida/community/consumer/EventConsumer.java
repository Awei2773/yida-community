package com.waigo.yida.community.consumer;

import com.alibaba.fastjson.JSONObject;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.entity.Message;
import com.waigo.yida.community.service.DiscussPostService;
import com.waigo.yida.community.service.ESDiscussPostService;
import com.waigo.yida.community.service.EventResolverChain;
import com.waigo.yida.community.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * author waigo
 * create 2021-10-23 20:59
 */
@Component
public class EventConsumer {
    @Autowired
    MessageService messageService;
    @Autowired
    EventResolverChain resolverChain;
    @Autowired
    ESDiscussPostService esDiscussPostService;
    @Autowired
    DiscussPostService discussPostService;
    private final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @KafkaListener(topics = {CommunityConstant.TOPIC_COMMENT, CommunityConstant.TOPIC_FOCUS, CommunityConstant.TOPIC_LIKE})
    public void handleMessage(ConsumerRecord msg) {
        Event event = validateMessage(msg);
        if (event == null) return;
        resolverChain.resolve(event);
        Message message = new Message();
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        message.setFromId(CommunityConstant.SYSTEM_USER_ID);//都是系统消息
        message.setToId(event.getUserId());
        message.setContent(JSONObject.toJSONString(event));
        //自己对自己造成的事件不发送
        if (event.getUserId() != event.getPostId()) {
            messageService.addMessage(message);
        }
    }

    @KafkaListener(topics = {CommunityConstant.TOPIC_UPDATE_POST})
    public void updateESDiscussPost(ConsumerRecord msg) {
        Event event = validateMessage(msg);
        if (event == null) return;
        esDiscussPostService.saveDiscussPost(discussPostService.getDiscussPost(event.getEntityId()));
    }

    /**
     * 消费删除帖子事件
     * @param msg
     */
    @KafkaListener(topics = {CommunityConstant.TOPIC_DELETE_POST})
    public void deleteESDiscussPost(ConsumerRecord msg) {
        Event event = validateMessage(msg);
        if (event == null) return;
        esDiscussPostService.deleteDiscussPostById(event.getEntityId());
    }


    private Event validateMessage(ConsumerRecord msg) {
        if (msg == null || msg.value() == null) {
            logger.error("消息为空！！！");
            return null;
        }
        Event event = JSONObject.parseObject((String) msg.value(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！！！[]", msg.value());
            return null;
        }
        return event;
    }
}
