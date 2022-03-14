package com.waigo.yida.community.entity;

/**
 * author waigo
 * create 2021-10-23 21:03
 */

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka交互时发送的事件对象
 * postId发给userId,针对entityType类型id为entityId的实体,通知类型为topic的通知,附加数据存在data中
 * 例如：用户237给用户108的帖子34进行了评论，那么Event就是如下组织
 * {topic:"COMMENT",entityType:1,entityId:34,postId:237,userId:108,data:{}}
 * data中可以添加一下评论的内容啥的
 */
public class Event {
    @JSONField(ordinal = 1)
    private String topic;
    /**
     * 事件的类型
     */
    @JSONField(ordinal = 2)
    private int entityType;
    /**
     * 事件对象实体的id，比如评论就是帖子的id
     */
    @JSONField(ordinal = 3)
    private int entityId;
    /**
     * 事件发起人
     */
    @JSONField(ordinal = 4)
    private int postId;
    /**
     * 事件接收人
     */
    @JSONField(ordinal = 5)
    private int userId;
    @JSONField(ordinal = 6)
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getPostId() {
        return postId;
    }

    public Event setPostId(int postId) {
        this.postId = postId;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event addData(String key,Object value) {
        this.data.put(key,value);
        return this;
    }
}
