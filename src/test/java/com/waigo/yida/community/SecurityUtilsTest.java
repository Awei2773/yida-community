package com.waigo.yida.community;

import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.service.UserService;
import com.waigo.yida.community.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * author waigo
 * create 2021-10-09 19:11
 */
@SpringBootTest
public class SecurityUtilsTest {
    public static void main(String[] args) {
        String aaa = SecurityUtil.encryptPassword("lhh", "11111111", "5abfc");
        System.out.println(aaa);
    }
    @Autowired
    UserService userService;
    @Test
    public void changeAllUserPassword(){
        for(int i = 1;i<=159;i++){
            User user = userService.getUser(i);
            if(user!=null){
                String nowPassword = SecurityUtil.encryptPassword(user.getUsername(), "11111111", user.getSalt());
                userService.changePassword(i,nowPassword);
            }
        }
    }
}
