package com.waigo.yida.community;

import com.waigo.yida.community.dao.UserMapper;
import com.waigo.yida.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest
class CommunityApplicationTests {

    @Test
    void contextLoads() {
    }
    @Autowired
    UserMapper userMapper;
    @Test
    public void testSelect(){
        User user = userMapper.selectByEmail("1617731215@qq.com");
        System.out.println(user);
        User user1 = userMapper.selectById(101);
        System.out.println(user1);
        User lisi = userMapper.selectByName("lisi");
        System.out.println(lisi);
    }
    @Test
    public void testInsert(){
        User user = new User();
        user.setCreateTime(new Date());
        user.setEmail("nowcoder@sina.com");
        user.setUsername("张三");
        System.out.println(userMapper.insertUser(user));
    }

}
