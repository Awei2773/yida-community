package com.waigo.yida.community.controller;

import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * author waigo
 * create 2021-10-03 13:13
 */
@Controller
public class RegisterController {
    @Autowired
    UserService userService;

    @GetMapping("/register")
    public String registerPage(){
        return "/site/register";
    }
    @PostMapping("/register")
    public String register(User user, Model model){
        Status status = userService.register(user);
        model.addAttribute("status",status);
        if(status.isSuccess()){
            //成功
            status.addAttribute("jumpText","恭喜您，已经注册成功，请尽快前往邮箱激活~~");
            status.addAttribute("path","/index");
            return "/site/operate-result";
        }else{
            //失败,回到注册页面
            status.addAttribute("jumpText","注册失败！！！");
            return "/site/register";
        }
    }
    //激活账号
    @GetMapping("/activation/{userId}/{activationCode}")
//    @CrossOrigin(originPatterns = {"*"},allowCredentials = "true",allowedHeaders = "*")
    public String activateUser(@PathVariable("userId") int userId,
                               @PathVariable("activationCode") String activationCode,Model model){
        Status status = userService.activate(userId,activationCode);
        model.addAttribute("status",status);
        status.addAttribute("path",status.isSuccess()?"/login":"/index");
        status.addAttribute("jumpText",status.isSuccess()?"恭喜您激活账户成功！！！"
                :"该账号已经激活过或者激活链接错误！！！");
        return "/site/operate-result";
    }
}
