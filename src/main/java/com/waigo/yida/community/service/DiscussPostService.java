package com.waigo.yida.community.service;

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.entity.DiscussPost;

import java.util.List;

/**
 * author waigo
 * create 2021-10-02 8:06
 */
public interface DiscussPostService {
    /**
     * 查找出当前帖子的页数
     * @return
     */
    int selectPageAll(Page page);

    /**
     * 根据分页信息和排序规则返回当前页
     * @param page
     * @return
     */
    List<DiscussPost> selectTargetPage(Page page);

    void addDiscussPost(DiscussPost discussPost);

    /**
     * 通过给定id，查找出一篇帖子
     * @param id
     * @return
     */
    DiscussPost getDiscussPost(int id);

    /**
     * 将id这个帖子的评论数+commentCount
     * @param id
     * @param commentCount
     * @return
     */
    int addCommentCount(int id, int commentCount);

    List<DiscussPost> listAll();

    void updatePostTypeById(int id, int type);

    void updatePostStatusById(int id, int status);

    void deletePostById(int id);
}
