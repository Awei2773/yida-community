package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.common.KafkaClient;
import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.dao.CommentMapper;
import com.waigo.yida.community.entity.Comment;
import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.service.CommentService;
import com.waigo.yida.community.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author waigo
 * create 2021-10-06 21:14
 */

/**
 * 评论相关的服务接口
 */
@Service
public class CommentServiceImpl implements CommentService, CommunityConstant {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    EventProducer eventProducer;
    @Override
    public List<Comment> selectComments(int entityType, int entityId, Page page) {
        int offset = page!=null?page.getOffset():0;
        int pageSize = page!=null?page.getPageSize():0;
        return commentMapper.selectComments(entityType,entityId,0,offset, pageSize);
    }

    @Override
    public int selectPageAll(int entityType,Page page) {
        int rows = commentMapper.selectCommentRowsByEntityId(entityType,page.getSourceId());
        page.setPageAllByRows(rows);
        return page.getPageAll();
    }

    @Override
    public List<Comment> selectReplys(int entityType, int targetId, Page page) {
        //回复不用做分页，全部查出来
        return commentMapper.selectComments(entityType,targetId,0,0,0);
    }

    @Override
    public Comment getCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }

    /**
     * 添加评论
     * @param comment
     * @return
     */
    @Override
    public int addComment(Comment comment) {
        //1.给帖子进行评论
        //2.给评论进行回复
        //将这个事件发送给kafka,这事件针对的是comment类型，事件发起人知道，目标人判断处理
        int rows = commentMapper.insertComment(comment);
        Event event = new Event();
        event.setTopic(CommunityConstant.TOPIC_COMMENT)
             .setPostId(comment.getUserId())
             .setEntityType(CommunityConstant.COMMENT)
             .setEntityId(comment.getId());
        eventProducer.publishEvent(event);
        return rows;
    }

    @Override
    public long selectCommentRowsByUserId(int userId,int entityType) {
        return commentMapper.selectCommentRowsByUserId(userId,entityType);
    }

    @Override
    public List<Comment> selectCommentPageByUserId(int userId, int entityType, Page page) {
        return commentMapper.selectComments(entityType,-1,userId,page.getOffset(),page.getPageSize());
    }
}
