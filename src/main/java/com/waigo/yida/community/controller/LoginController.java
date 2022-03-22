package com.waigo.yida.community.controller;

import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.config.properties.KaptchaProperties;
import com.waigo.yida.community.constant.AuthConstant;
import com.waigo.yida.community.log.annotation.LogUserOpt;
import com.waigo.yida.community.log.enums.UserOption;
import com.waigo.yida.community.service.AuthService;
import com.waigo.yida.community.service.KaptchaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * author waigo
 * create 2021-10-03 15:37
 */
@Controller
public class LoginController implements AuthConstant {
    @Autowired
    @Qualifier("img-kaptcha")
    KaptchaService kaptchaService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @Autowired
    AuthService authService;
    @Value("${server.host}")
    String host;
    @Value("${server.servlet.context-path}")
    String contextPath;

    @Autowired
    TransactionTemplate transactionTemplate;

    @LogUserOpt(UserOption.LOGIN)
    @PostMapping("/login")
//    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public String login(String username, String password, String captcha, boolean remember, HttpSession session, Model model, HttpServletResponse response) {
        if (!kaptchaService.checkKaptcha(captcha, session)) {
            model.addAttribute("captchaMsg", "验证码校验不通过！！！");
            return "/site/login";
        }
        int maxAge = remember ? REMEMBER_ME_EXPIRE_TIME : COMMON_EXPIRE_TIME;

        //1.事务控制
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        Status status = transactionTemplate.execute(transactionStatus -> authService.login(username, password, maxAge));
        //记住我功能-老版实现
        if (status!=null&&status.isSuccess()) {
            //1.设置cookie
            Cookie ticket = new Cookie("ticket", (String) status.get("ticket"));
            ticket.setMaxAge(maxAge);
            ticket.setDomain(host);//哪些域名下生效
            ticket.setPath(contextPath);//哪些路径有效
            ticket.setSecure(false);
            response.addCookie(ticket);
            return "redirect:/index";
        }
        //2.将status进行收集，展示失败信息
        model.addAttribute("status", status);
        return "/site/login";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket, Model model) {
        if (StringUtils.isBlank(ticket) || !authService.isLogin(ticket,null)) {
            Status failure = Status.failure();
            failure.addAttribute("jumpText", "还未登录，请先进行登录！！！");
            failure.addAttribute("path", "/login");
            model.addAttribute("status", failure);

        } else {
            //退出登录
            authService.logout(ticket);
            Status success = Status.success();
            success.addAttribute("jumpText", "退出登录成功~~~");
            success.addAttribute("path", "/index");
            model.addAttribute("status", success);
        }
        return "/site/operate-result";
    }
}
