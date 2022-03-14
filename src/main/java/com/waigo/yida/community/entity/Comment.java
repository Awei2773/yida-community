package com.waigo.yida.community.entity;

import java.util.Date;

/**
 * author waigo
 * create 2021-10-06 20:20
 */
public class Comment {
    private int id;
    private int userId;
    /**
     * 如果是评论，这个type就是post,表示的是作用对象的类型，回复的作用对象是评论
     */
    private int entityType;
    private int entityId;
    /**
     * 如果是回复，这个targetId是回复对象的userId,评论和回复就在于一个有对象，一个没有
     */
    private int targetId;
    private String content;
    private int status;//0:正常，1:删除
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" + "id=" + id + ", userId=" + userId + ", entityType=" + entityType + ", entityId=" + entityId + ", targetId=" + targetId + ", content='" + content + '\'' + ", status=" + status + ", createTime=" + createTime + '}';
    }
}
