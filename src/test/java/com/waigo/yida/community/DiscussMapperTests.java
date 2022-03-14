package com.waigo.yida.community;

import com.waigo.yida.community.dao.DiscussPostMapper;
import com.waigo.yida.community.dao.UserMapper;
import com.waigo.yida.community.entity.DiscussPost;
import com.waigo.yida.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class DiscussMapperTests {

    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    public void testSelect(){
    }
    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPostsByHotOrTime(0, 0, 10,1);
        for(DiscussPost post : list) {
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }
    @Test
    public void testAddCommentCount(){
        discussPostMapper.incrCommmentCountById(109,-1);
    }
}
