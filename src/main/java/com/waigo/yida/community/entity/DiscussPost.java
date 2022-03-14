package com.waigo.yida.community.entity;

/**
 * author waigo
 * create 2021-10-01 21:30
 */

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * 讨论贴
 * 对应数据库表 `discuss_post`
 */
@Document(indexName="discuss-post")
public class DiscussPost {
    @Id
    private int id;
    @Field(type= FieldType.Integer)
    private int userId;
    @Field(type=FieldType.Text,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    private String title;
    @Field(type=FieldType.Text,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    private String content;
    @Field(type= FieldType.Integer)
    private int type;
    @Field(type= FieldType.Integer)
    private int status;
    @Field(type=FieldType.Date)
    private Date createTime;
    /**评论数*/
    @Field(type= FieldType.Integer)
    private int commentCount;
    /**点赞数*/
    @Field(type= FieldType.Long)
    private long likeCount;
    /** 点赞状态:当前查看的用户是否点过赞*/
    @Field(type= FieldType.Integer)
    private int likeStatus;
    @Field(type= FieldType.Double)
    private double score;

    private User user;

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    @Override
    public String toString() {
        return "DiscussPost{" + "id=" + id + ", userId=" + userId + ", title='" + title + '\'' + ", content='" + content + '\'' + ", type=" + type + ", status=" + status + ", createTime=" + createTime + ", commentCount=" + commentCount + ", likeCount=" + likeCount + ", score=" + score + ", user=" + user + '}';
    }

}