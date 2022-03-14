package com.waigo.yida.community.vo;

/**
 * author waigo
 * create 2021-10-16 11:10
 */

import com.waigo.yida.community.entity.User;

import java.util.Date;

/**
 * 描述粉丝和关注用户信息的vo
 */
public class FollowVO {
    private User user;
    /**
     * 当前用户是否关注过这个列表里的用户，需要注意的是当前登录用户肯定是关注了自己关注列表里的所有用户，这时就不用再费劲去查了
     */
    private boolean isFollower;
    private Date followedDate;

    public Date getFollowedDate() {
        return followedDate;
    }

    public void setFollowedDate(Date followedDate) {
        this.followedDate = followedDate;
    }

    @Override
    public String toString() {
        return "FollowVO{" + "user=" + user + ", isFollower=" + isFollower + '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }

    public FollowVO(User user, boolean isFollower, Date followedDate) {
        this.user = user;
        this.isFollower = isFollower;
        this.followedDate = followedDate;
    }

}
