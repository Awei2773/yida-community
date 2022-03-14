package com.waigo.yida.community;

import com.waigo.yida.community.dao.CommentMapper;
import com.waigo.yida.community.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * author waigo
 * create 2021-10-06 22:11
 */
@SpringBootTest
public class CommentMapperTest {
    @Autowired
    CommentMapper commentMapper;
    /*List<Comment> selectComments(int entityType, int entityId, int offset, int pageSize,
                                 boolean isAsc, @Param("orderColumns") String... orderColumns);*/
    @Test
    public void testSelectComments(){
        List<Comment> comments = commentMapper.selectComments(1, 270, 0, 5);
        System.out.println(comments);
    }
    /*int selectCommentRowsByEntityId(int entityId);*/
    @Test
    public void testSelectCommentRowsByEntityId(){
        int i = commentMapper.selectCommentRowsByEntityId(1,270);
        System.out.println(i);
    }
}
