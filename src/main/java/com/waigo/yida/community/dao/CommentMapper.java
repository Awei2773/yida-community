package com.waigo.yida.community.dao;


import com.waigo.yida.community.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * author waigo
 * create 2021-10-06 21:45
 */
public interface CommentMapper {
    /**
     * 按照创建时间进行排序
     * @param entityType 评论类别,传0则查出全部
     * @param id   如果是评论：所属类别对象entityId，如果是回复：这是targetId,-1则是查出全部
     * @param offset   偏移量
     * @param userId 用户id
     * @param limit   查出几条
     * @return
     */
    List<Comment> selectComments(int entityType, int id,int userId,int offset, int limit);

    /**
     * 查出该类型id下的所有评论记录条数，比如200号帖子的记录条数就是传200进来
     * @param entityId
     * @param entityType
     * @return
     */
    int selectCommentRowsByEntityId(int entityType,int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

    /**
     * 查出该用户发的评论和回复数量
     * @param userId
     * @return
     */
    int selectCommentRowsByUserId(int userId,int entityType);
}
