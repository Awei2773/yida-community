package com.waigo.yida.community.service;

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.entity.Comment;

import java.util.List;

/**
 * author waigo
 * create 2021-10-06 21:02
 */
public interface CommentService {
    /**
     * 根据评论的类型和该类型下的所属id查出对应的评论列表
     * @param entityType 评论类型id,关联CommunityConstant中的类型常量
     * @param entityId   该类型的id，比如帖子类型，这个帖子的id，查出这个帖子的所有评论
     * @param page 分页信息
     * @return
     */
    List<Comment> selectComments(int entityType, int entityId, Page page);

    /**
     * 按照分页信息查出当前页数
     * @param page
     * @return
     */
    int selectPageAll(int entityType,Page page);

    List<Comment> selectReplys(int entityType, int targetId, Page page);

    Comment getCommentById(int id);

    int addComment(Comment comment);

    long selectCommentRowsByUserId(int userId,int entityType);

    /**
     * 查出某个用户评论或者回复类型的分页数据
     * @param userId
     * @param entityType
     * @param page
     * @return
     */
    List<Comment> selectCommentPageByUserId(int userId, int entityType, Page page);
}
