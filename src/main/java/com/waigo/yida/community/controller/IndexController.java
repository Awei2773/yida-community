package com.waigo.yida.community.controller;

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.entity.DiscussPost;
import com.waigo.yida.community.service.DiscussPostService;
import com.waigo.yida.community.service.LikeService;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author waigo
 * create 2021-10-01 23:51
 */
@Controller
public class IndexController {
    /**讨论帖的服务接口*/
    @Autowired
    DiscussPostService discussPostService;
    /** 点赞服务接口*/
    @Autowired
    LikeService likeService;
    @GetMapping(path={"/","index.html","index"})
    public String getIndexPage(Model model, Page page, HttpServletRequest request){
        int pageAll = discussPostService.selectPageAll(page);
        page.setPageAll(pageAll);
        page.setPath(request.getServletPath());
        page.checkCurrent();
        page.checkPageFrom();
        List<DiscussPost> curPage = discussPostService.selectTargetPage(page);
        curPage.forEach(discussPost ->
                discussPost.setLikeCount(
                        likeService.likeCount(CommunityConstant.DISCUSS_POST, discussPost.getId())
                ));
        model.addAttribute("curPage",curPage);
        return "index";
    }
    @GetMapping(path = {"/test-sse"})
    public String returnSsePage(){
        return "/test-sse";
    }
}
