package com.waigo.yida.community.controller;

import com.waigo.yida.community.config.properties.KaptchaProperties;
import com.waigo.yida.community.service.KaptchaService;
import com.waigo.yida.community.service.UserService;
import com.waigo.yida.community.util.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * author waigo
 * create 2021-10-09 16:50
 * 提供验证码的控制器
 */
@Controller
public class KaptchaController {
    @Autowired
    @Qualifier("img-kaptcha")
    KaptchaService kaptchaService;

    @Autowired
    UserService userService;

    @Autowired
    @Qualifier("email-captcha")
    KaptchaService emailCaptcha;
    @Autowired
    KaptchaProperties properties;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/img/captcha")
    @ResponseBody
    public void getCaptcha(HttpSession session, HttpServletResponse response) {
        BufferedImage kaptcha = kaptchaService.createKaptcha(session);
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(kaptcha, properties.getImgType(), os);
        } catch (IOException e) {
            LOGGER.error("响应流出现问题:{}", e.getMessage());
        }
    }
    @PostMapping("/email/captcha")
    @ResponseBody
    public String getEmailCaptcha(String email,HttpSession session){
        if(StringUtils.isBlank(email)){
            return R.create(400,"邮箱不能为空！！！").toString();
        }
        if(!userService.containsEmail(email)){
            return R.create(400,"该邮箱未注册！！！").toString();
        }
        String verifyCode = emailCaptcha.createEmailKaptcha(session, email);
        return R.create(200,"OK").addAttribute("verifyCode",verifyCode).toString();
    }
}
