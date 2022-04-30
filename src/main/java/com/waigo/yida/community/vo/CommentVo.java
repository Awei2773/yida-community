package com.waigo.yida.community.vo;

import com.waigo.yida.community.entity.Comment;
import com.waigo.yida.community.entity.DiscussPost;
import com.waigo.yida.community.entity.User;

import java.util.List;

/**
 * author waigo
 * create 2021-10-06 20:20
 */
public class CommentVo {
    /**
     * 评论的发表人
     * */
    private User user;
    /**
     * 如果是回复，还可能存在目标对象,如果没有就为null
     */
    private User targetUser;
    /**
     * 评论内容
     */
    private Comment comment;
    /**
     * 评论的回复列表
     */
    private List<CommentVo> replys;

    /**
     * 评论的点赞数
     */
    private long likeCount;

    /**
     * likeStatus,当前查看的这个用户针对这个评论是否点过赞
     */
    private int isLike;

    /**
     * 评论所属的帖子对象
     */
    private DiscussPost post;
    public CommentVo() {
    }

    public CommentVo(User user, Comment comment, List<CommentVo> replys, long likeCount,int isLike) {
        this.user = user;
        this.comment = comment;
        this.replys = replys;
        this.likeCount = likeCount;
        this.isLike = isLike;
    }

    public CommentVo(User user, Comment comment, List<CommentVo> replys) {
        this.user = user;
        this.comment = comment;
        this.replys = replys;
    }

    public CommentVo(User user, User targetUser, Comment comment,long likeCount,int isLike) {
        this.user = user;
        this.targetUser = targetUser;
        this.comment = comment;
        this.likeCount = likeCount;
        this.isLike = isLike;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public List<CommentVo> getReplys() {
        return replys;
    }

    public void setReplys(List<CommentVo> replys) {
        this.replys = replys;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public DiscussPost getPost() {
        return post;
    }

    public void setPost(DiscussPost post) {
        this.post = post;
    }
}
