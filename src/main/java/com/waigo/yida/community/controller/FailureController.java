package com.waigo.yida.community.controller;

import com.waigo.yida.community.common.Status;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


/**
 * author waigo
 * create 2022-04-29 11:38
 */
@Controller
public class FailureController {
    @GetMapping("/denied")
    public String getDeniedPage(HttpServletRequest request, Model model){
        Status failure = Status.failure().lineAddAttribute("jumpText", "您没有访问该功能的权限").lineAddAttribute("path", request.getContextPath() + "/login.html");
        model.addAttribute("status", failure);
        return "/site/operate-result";
    }
}
